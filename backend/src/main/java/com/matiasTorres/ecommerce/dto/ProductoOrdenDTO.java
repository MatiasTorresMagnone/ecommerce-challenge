package com.matiasTorres.ecommerce.dto;

import java.math.BigDecimal;

public record ProductoOrdenDTO(
        Long idProducto,
        String nombreProducto,
        int cantidad,
        BigDecimal precioUnitario,
        BigDecimal subtotal,
        String imagen
) {
}
