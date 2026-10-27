package com.matiasTorres.ecommerce.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(MailYaExisteException.class)
    public ResponseEntity<String> handleMailYaExiste(MailYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ProductoNoEncontradoException.class)
    public ResponseEntity<String> handleProductoNoEncontrado(ProductoNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<String> handleValidacion(MethodArgumentNotValidException ex) {
        String mensaje = ex.getBindingResult().getFieldErrors().stream()
                .map(e -> e.getField() + ": " + e.getDefaultMessage())
                .collect(Collectors.joining(", "));
        return ResponseEntity.badRequest().body(mensaje);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handleCredencialesInvalidas(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Mail o contraseña incorrectos");
    }

    @ExceptionHandler(ItemNoEncontradoException.class)
    public ResponseEntity<String> handleItemNoEncontrado(ItemNoEncontradoException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(ProductoYaExisteException.class)
    public ResponseEntity<String> handleProductoYaExiste(ProductoYaExisteException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ex.getMessage());
    }

    @ExceptionHandler(ArchivoInvalidoException.class)
    public ResponseEntity<String> handleArchivoInvalido(ArchivoInvalidoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(ImagenNoEncontradaException.class)
    public ResponseEntity<String> handleImagenNoEncontrada(ImagenNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<String> handleArchivoGrande(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.CONTENT_TOO_LARGE).body("El archivo supera el máximo de 2 MB");
    }

    @ExceptionHandler(OrdenNoEncontradaException.class)
    public ResponseEntity<String> handleOrdenNoEncontrada(OrdenNoEncontradaException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(CheckoutInvalidoException.class)
    public ResponseEntity<String> handleCheckoutInvalido(CheckoutInvalidoException ex) {
        return ResponseEntity.badRequest().body(ex.getMessage());
    }

    @ExceptionHandler(PagoException.class)
    public ResponseEntity<String> handlePago(PagoException ex) {
        return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                .body("No se pudo iniciar el pago. Intentá de nuevo en unos minutos.");
    }

    @ExceptionHandler(StripeNoConfiguradoException.class)
    public ResponseEntity<String> handleStripeNoConfigurado(StripeNoConfiguradoException ex) {
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(ex.getMessage());
    }
}
