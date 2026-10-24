package com.matiasTorres.ecommerce.dto;

import com.matiasTorres.ecommerce.model.enums.Rol;

public record UsuarioResponseDTO(
        Long id,
        String mail,
        Rol rol
) {
}
