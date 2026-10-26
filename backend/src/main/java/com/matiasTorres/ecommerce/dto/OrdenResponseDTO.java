package com.matiasTorres.ecommerce.dto;

import com.matiasTorres.ecommerce.model.enums.EstadoOrden;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record OrdenResponseDTO(
    Long idOrden,
    BigDecimal total,
    EstadoOrden estado,
    LocalDateTime fechaCreacion
) {
}
