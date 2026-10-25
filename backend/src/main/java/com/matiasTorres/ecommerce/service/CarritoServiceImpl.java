package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.CarritoItemResponseDTO;
import com.matiasTorres.ecommerce.dto.CarritoRequestDTO;
import com.matiasTorres.ecommerce.dto.CarritoResponseDTO;
import com.matiasTorres.ecommerce.exception.ItemNoEncontradoException;
import com.matiasTorres.ecommerce.exception.ProductoNoEncontradoException;
import com.matiasTorres.ecommerce.model.Carrito;
import com.matiasTorres.ecommerce.model.CarritoItem;
import com.matiasTorres.ecommerce.model.Producto;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.repository.CarritoItemRepo;
import com.matiasTorres.ecommerce.repository.CarritoRepo;
import com.matiasTorres.ecommerce.repository.ProductoRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CarritoServiceImpl implements CarritoService{

    private final CarritoRepo carritoRepo;
    private final CarritoItemRepo carritoItemRepo;
    private final ProductoRepo productoRepo;


    @Override
    @Transactional(readOnly = true)
    public CarritoResponseDTO verCarrito(Usuario usuario) {
        Optional<Carrito> carrito = carritoRepo.findByUsuario(usuario);
        if (carrito.isEmpty()) {
            return new CarritoResponseDTO(List.of(), BigDecimal.ZERO);
        }

        List<CarritoItemResponseDTO> items = carritoItemRepo.findByCarrito(carrito.get()).stream()
                .map(this::toItemDTO)
                .toList();

        BigDecimal total = items.stream()
                .map(CarritoItemResponseDTO::subtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        return new CarritoResponseDTO(items, total);
    }

    @Override
    @Transactional
    public CarritoResponseDTO agregarProducto(Usuario usuario, CarritoRequestDTO request) {
        Carrito carrito = carritoRepo.findByUsuario(usuario).orElseGet(() -> {
            Carrito nuevo = new Carrito();
            nuevo.setUsuario(usuario);
            return carritoRepo.save(nuevo);
        });

        Producto producto = productoRepo.findByIdProductoAndActivoTrue(request.productoId())
                .orElseThrow(() -> new ProductoNoEncontradoException("Producto no encontrado"));

        CarritoItem item = carritoItemRepo.findByCarritoAndProducto(carrito, producto)
                .orElseGet(() -> {
                    CarritoItem nuevo = new CarritoItem();
                    nuevo.setCarrito(carrito);
                    nuevo.setProducto(producto);
                    nuevo.setCantidad(0);
                    return nuevo;
                });

        item.setCantidad(item.getCantidad() + request.cantidad());
        carritoItemRepo.save(item);
        return verCarrito(usuario);
    }

    @Override
    @Transactional
    public CarritoResponseDTO modificarCantidad(Usuario usuario, Long idItem, int cantidad) {
        CarritoItem item = carritoItemRepo.findById(idItem)
                .filter(i -> i.getCarrito().getUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
                .orElseThrow(() -> new ItemNoEncontradoException("Ítem no encontrado"));

        item.setCantidad(cantidad);
        carritoItemRepo.save(item);

        return verCarrito(usuario);
    }

    @Override
    @Transactional
    public CarritoResponseDTO eliminarItem(Usuario usuario, Long idItem) {
        CarritoItem item = carritoItemRepo.findById(idItem)
                .filter(i -> i.getCarrito().getUsuario().getIdUsuario().equals(usuario.getIdUsuario()))
                .orElseThrow(() -> new ItemNoEncontradoException("Ítem no encontrado"));
        carritoItemRepo.delete(item);

        return verCarrito(usuario);
    }


    private CarritoItemResponseDTO toItemDTO(CarritoItem item) {
        Producto p = item.getProducto();
        BigDecimal subtotal = p.getPrecio().multiply(BigDecimal.valueOf(item.getCantidad()));
        return new CarritoItemResponseDTO(
                item.getIdCarritoItem(),
                p.getIdProducto(),
                p.getNombre(),
                p.getImagen(),
                p.getPrecio(),
                item.getCantidad(),
                subtotal);
    }
}
