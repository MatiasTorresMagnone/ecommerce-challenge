package com.matiasTorres.ecommerce.dto;

import com.matiasTorres.ecommerce.model.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record OrdenDetalleResponseDTO(
        Long idOrden,
        EstadoOrden estado,
        LocalDateTime fechaCreacion,
        BigDecimal total,
        List<ProductoOrdenDTO> productos

) {
}
