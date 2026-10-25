package com.matiasTorres.ecommerce.dto;

import jakarta.validation.constraints.Min;

public record ModificarCantidadRequestDTO(
        @Min(value = 1, message = "La cantidad debe ser al menos 1")
        int cantidad
) {
}
