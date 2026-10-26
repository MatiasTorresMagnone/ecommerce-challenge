package com.matiasTorres.ecommerce.dto;

import java.math.BigDecimal;

public record ProductoResponseAdminDTO(
        Long id,
        String nombre,
        String descripcion,
        BigDecimal precio,
        String imagen,
        Boolean activo
) {
}
