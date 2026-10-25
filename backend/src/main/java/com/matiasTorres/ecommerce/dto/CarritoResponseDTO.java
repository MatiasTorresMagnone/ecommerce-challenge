package com.matiasTorres.ecommerce.dto;

import java.math.BigDecimal;
import java.util.List;

public record CarritoResponseDTO(
        List<CarritoItemResponseDTO> items,
        BigDecimal total
) {
}