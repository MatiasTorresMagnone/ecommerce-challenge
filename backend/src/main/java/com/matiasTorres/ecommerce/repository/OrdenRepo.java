package com.matiasTorres.ecommerce.repository;

import com.matiasTorres.ecommerce.model.Orden;
import com.matiasTorres.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdenRepo extends JpaRepository<Orden,Long> {
    List<Orden> findByUsuarioOrderByFechaCreacionDesc(Usuario usuario);


    Optional<Orden> findByIdOrdenAndUsuario(Long idOrden, Usuario usuario);
}
