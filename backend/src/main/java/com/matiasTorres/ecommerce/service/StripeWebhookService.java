package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.exception.PagoException;
import com.matiasTorres.ecommerce.exception.StripeNoConfiguradoException;
import com.matiasTorres.ecommerce.model.Orden;
import com.matiasTorres.ecommerce.model.enums.EstadoOrden;
import com.matiasTorres.ecommerce.repository.CarritoItemRepo;
import com.matiasTorres.ecommerce.repository.CarritoRepo;
import com.matiasTorres.ecommerce.repository.OrdenRepo;
import com.stripe.exception.EventDataObjectDeserializationException;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.StripeObject;
import com.stripe.model.checkout.Session;
import com.stripe.net.Webhook;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class StripeWebhookService {
    private final OrdenRepo ordenRepo;
    private final CarritoRepo carritoRepo;
    private final CarritoItemRepo carritoItemRepo;

    @Value("${stripe.webhook-secret:}")
    private String webhookSecret;

    @Transactional
    public void procesar(String payload, String firma) throws SignatureVerificationException {
        if (webhookSecret == null || webhookSecret.isBlank()) {
            throw new StripeNoConfiguradoException("El webhook no está configurado en este entorno");
        }

        // Lanza SignatureVerificationException si la firma no coincide
        Event event = Webhook.constructEvent(payload, firma, webhookSecret);

        switch (event.getType()) {
            case "checkout.session.completed" -> pagoCompletado(obtenerSession(event));
            case "checkout.session.expired" -> sesionExpirada(obtenerSession(event));
            default -> log.debug("Evento ignorado: {}", event.getType());
        }
    }

    private void pagoCompletado(Session session) {
        Optional<Orden> encontrada = ordenRepo.findByStripeSessionId(session.getId());
        if (encontrada.isEmpty()) {
            log.warn("Sesión {} sin orden asociada", session.getId());
            return;
        }
        Orden orden = encontrada.get();

        // Idempotencia: Stripe puede reenviar el mismo evento
        if (orden.getEstado() != EstadoOrden.PENDIENTE) {
            log.info("Orden {} ya procesada ({})", orden.getIdOrden(), orden.getEstado());
            return;
        }
        if (!"paid".equals(session.getPaymentStatus())) {
            log.info("Sesión {} completada pero sin pago confirmado", session.getId());
            return;
        }

        orden.setEstado(EstadoOrden.PAGADA);
        ordenRepo.save(orden);
        carritoRepo.findByUsuario(orden.getUsuario())
                .ifPresent(carritoItemRepo::deleteByCarrito);
        log.info("Orden {} pagada", orden.getIdOrden());
    }

    private void sesionExpirada(Session session) {
        ordenRepo.findByStripeSessionId(session.getId()).ifPresent(orden -> {
            if (orden.getEstado() == EstadoOrden.PENDIENTE) {
                orden.setEstado(EstadoOrden.CANCELADA);
                ordenRepo.save(orden);
                log.info("Orden {} cancelada por expiración", orden.getIdOrden());
            }
        });
    }

    private Session obtenerSession(Event event) {
        EventDataObjectDeserializer deserializer = event.getDataObjectDeserializer();
        try {
            StripeObject objeto = deserializer.getObject().isPresent()
                    ? deserializer.getObject().get()
                    : deserializer.deserializeUnsafe();
            return (Session) objeto;
        } catch (EventDataObjectDeserializationException e) {
            throw new PagoException("No se pudo leer el evento de Stripe", e);
        }
    }
}
