package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.CarritoRequestDTO;
import com.matiasTorres.ecommerce.dto.CarritoResponseDTO;
import com.matiasTorres.ecommerce.dto.ModificarCantidadRequestDTO;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.service.CarritoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping("/carrito")
public class CarritoController {

    private final CarritoService carritoService;

    @GetMapping
    public ResponseEntity<CarritoResponseDTO> verCarrito(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(carritoService.verCarrito(usuario));
    }

    @PostMapping("/items")
    public ResponseEntity<CarritoResponseDTO> agregarProducto(@AuthenticationPrincipal Usuario usuario, @Valid @RequestBody CarritoRequestDTO request){
        return ResponseEntity.status(HttpStatus.CREATED).body(carritoService.agregarProducto(usuario, request));
    }

    @PutMapping("/items/{idItem}")
    public ResponseEntity<CarritoResponseDTO> modificarCantidad(@AuthenticationPrincipal Usuario usuario, @PathVariable Long idItem, @Valid @RequestBody ModificarCantidadRequestDTO request) {
        return ResponseEntity.ok(carritoService.modificarCantidad(usuario, idItem, request.cantidad()));
    }

    @DeleteMapping("/items/{idItem}")
    public ResponseEntity<CarritoResponseDTO> eliminarProductoCarrito(@AuthenticationPrincipal Usuario usuario, @PathVariable Long idItem){
        return ResponseEntity.ok(carritoService.eliminarItem(usuario, idItem));
    }
}
