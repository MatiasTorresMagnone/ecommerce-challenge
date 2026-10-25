package com.matiasTorres.ecommerce.repository;

import com.matiasTorres.ecommerce.model.Producto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductoRepo extends JpaRepository<Producto,Long>  {
    List<Producto> findAllByActivoTrue();
    Optional<Producto> findByIdProductoAndActivoTrue(Long id);

    List<Producto> findByNombreContainingIgnoreCaseAndActivoTrue(String nombre);

}
