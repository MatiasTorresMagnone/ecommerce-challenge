package com.matiasTorres.ecommerce.repository;


import com.matiasTorres.ecommerce.model.Carrito;
import com.matiasTorres.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CarritoRepo extends JpaRepository<Carrito,Long> {
    Optional<Carrito> findByUsuario(Usuario usuario);
}
