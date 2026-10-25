package com.matiasTorres.ecommerce.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public record CarritoRequestDTO(
        @NotNull Long productoId,
        @Min(1) int cantidad
) {
}
