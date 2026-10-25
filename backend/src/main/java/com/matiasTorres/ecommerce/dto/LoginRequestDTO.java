package com.matiasTorres.ecommerce.dto;


import jakarta.validation.constraints.NotBlank;

public record LoginRequestDTO(
        @NotBlank(message = "El mail es obligatorio")
        String mail,

        @NotBlank(message = "La contraseña es obligatoria")
        String password
) {
}
