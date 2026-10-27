package com.matiasTorres.ecommerce.controller;


import com.stripe.exception.SignatureVerificationException;
import com.matiasTorres.ecommerce.service.StripeWebhookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/webhooks")
public class StripeWebhookController {

    private final StripeWebhookService webhookService;

    @PostMapping("/stripe")
    public ResponseEntity<String> recibir(@RequestBody String payload,
                                          @RequestHeader("Stripe-Signature") String firma) {
        try {
            webhookService.procesar(payload, firma);
            return ResponseEntity.ok("ok");
        } catch (SignatureVerificationException e) {
            log.warn("Webhook con firma inválida");
            return ResponseEntity.badRequest().body("Firma inválida");
        }
    }
}
