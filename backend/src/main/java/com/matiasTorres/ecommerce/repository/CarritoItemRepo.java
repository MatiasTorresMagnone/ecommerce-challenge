package com.matiasTorres.ecommerce.repository;

import com.matiasTorres.ecommerce.model.Carrito;
import com.matiasTorres.ecommerce.model.CarritoItem;
import com.matiasTorres.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;


public interface CarritoItemRepo extends JpaRepository<CarritoItem,Long> {
    List<CarritoItem> findByCarrito(Carrito carrito);

    Optional<CarritoItem> findByCarritoAndProducto(Carrito carrito, Producto producto);

    void deleteByCarrito(Carrito carrito);
}
