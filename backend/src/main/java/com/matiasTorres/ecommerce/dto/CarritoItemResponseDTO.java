package com.matiasTorres.ecommerce.dto;

import java.math.BigDecimal;

public record CarritoItemResponseDTO(
        Long idItem,
        Long productoId,
        String nombre,
        String imagen,
        BigDecimal precioUnitario,
        int cantidad,
        BigDecimal subtotal
) {
}
