package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.CheckoutResponseDTO;
import com.matiasTorres.ecommerce.exception.CheckoutInvalidoException;
import com.matiasTorres.ecommerce.exception.PagoException;
import com.matiasTorres.ecommerce.exception.StripeNoConfiguradoException;
import com.matiasTorres.ecommerce.model.*;
import com.matiasTorres.ecommerce.model.enums.EstadoOrden;
import com.matiasTorres.ecommerce.repository.CarritoItemRepo;
import com.matiasTorres.ecommerce.repository.CarritoRepo;
import com.matiasTorres.ecommerce.repository.OrdenRepo;
import com.stripe.exception.StripeException;
import com.stripe.net.RequestOptions;
import com.stripe.param.checkout.SessionCreateParams;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.stripe.model.checkout.Session;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService{
    private final CarritoRepo carritoRepo;
    private final CarritoItemRepo carritoItemRepo;
    private final OrdenRepo ordenRepo;

    @Value("${stripe.secret-key:}")
    private String secretKey;

    @Value("${app.frontend-url}")
    private String frontendUrl;

    @Override
    @Transactional
    public CheckoutResponseDTO iniciarCheckout(Usuario usuario) {
        if (secretKey == null || secretKey.isBlank()) {
            throw new StripeNoConfiguradoException("El pago no está configurado en este entorno");
        }

        Carrito carrito = carritoRepo.findByUsuario(usuario)
                .orElseThrow(() -> new CheckoutInvalidoException("El usuario no tiene un carrito"));
        List<CarritoItem> items = carritoItemRepo.findByCarrito(carrito);
        if (items.isEmpty()) {
            throw new CheckoutInvalidoException("El carrito está vacío");
        }

        Orden orden = new Orden();
        orden.setUsuario(usuario);
        orden.setEstado(EstadoOrden.PENDIENTE);

        BigDecimal total = BigDecimal.ZERO;
        for (CarritoItem item : items) {
            Producto p = item.getProducto();
            if (!p.isActivo()) {
                throw new CheckoutInvalidoException(
                        "El producto \"" + p.getNombre() + "\" ya no está disponible");
            }
            BigDecimal subtotal = p.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));

            OrdenDetalle detalle = new OrdenDetalle();
            detalle.setOrden(orden);
            detalle.setProducto(p);
            detalle.setCantidad(item.getCantidad());
            detalle.setPrecioUnitario(p.getPrecio());
            detalle.setSubtotal(subtotal);
            orden.getDetalles().add(detalle);

            total = total.add(subtotal);
        }
        orden.setTotal(total);
        orden = ordenRepo.save(orden);

        try {
            SessionCreateParams.Builder params = SessionCreateParams.builder()
                    .setMode(SessionCreateParams.Mode.PAYMENT)
                    .setSuccessUrl(frontendUrl + "/checkout/exito?orden=" + orden.getIdOrden())
                    .setCancelUrl(frontendUrl + "/checkout/cancelado?orden=" + orden.getIdOrden())
                    .setClientReferenceId(String.valueOf(orden.getIdOrden()))
                    .setCustomerEmail(usuario.getEmail());

            for (OrdenDetalle d : orden.getDetalles()) {
                params.addLineItem(SessionCreateParams.LineItem.builder()
                        .setQuantity((long) d.getCantidad())
                        .setPriceData(SessionCreateParams.LineItem.PriceData.builder()
                                .setCurrency("usd")
                                .setUnitAmount(aCentavos(d.getPrecioUnitario()))
                                .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                        .setName(d.getProducto().getNombre())
                                        .build())
                                .build())
                        .build());
            }

            RequestOptions options = RequestOptions.builder().setApiKey(secretKey).build();
            Session session = Session.create(params.build(), options);

            orden.setStripeSessionId(session.getId());
            return new CheckoutResponseDTO(session.getUrl(), orden.getIdOrden());

        } catch (StripeException e) {
            throw new PagoException("Error al crear la sesión de Stripe", e);
        }

    }

    private long aCentavos(BigDecimal precio) {
        return precio.movePointRight(2).setScale(0, RoundingMode.HALF_UP).longValueExact();
    }
}
