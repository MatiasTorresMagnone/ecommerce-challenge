package com.matiasTorres.ecommerce.repository;

import com.matiasTorres.ecommerce.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface UsuarioRepo extends JpaRepository<Usuario,Long> {
    Optional<Usuario> findByEmail(String email);
    Boolean existsByEmail(String mail);
}
