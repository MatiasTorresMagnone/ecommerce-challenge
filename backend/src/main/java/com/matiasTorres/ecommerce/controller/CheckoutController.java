package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.CheckoutResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.service.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/checkout")
public class CheckoutController {
    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<CheckoutResponseDTO> iniciar(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(checkoutService.iniciarCheckout(usuario));
    }
}
