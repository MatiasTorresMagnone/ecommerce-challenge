package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.ProductoRequestAdminDTO;
import com.matiasTorres.ecommerce.dto.ProductoResponseAdminDTO;
import com.matiasTorres.ecommerce.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/admin/productos")
@PreAuthorize("hasRole('ADMIN')")
public class AdministradorController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseAdminDTO>> verTodosLosProductos(){
        List<ProductoResponseAdminDTO> productos = productoService.findAll();
        return ResponseEntity.ok(productos);
    }

    @PostMapping
    public ResponseEntity<ProductoResponseAdminDTO> agregarProducto(@Valid @RequestBody ProductoRequestAdminDTO producto){
        return ResponseEntity.status(HttpStatus.CREATED).body(productoService.agregarProductoAdmin(producto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseAdminDTO> modificarProducto(@PathVariable Long id, @Valid @RequestBody ProductoRequestAdminDTO productoModificado){
        return ResponseEntity.ok(productoService.modificarProductoAdmin(id,productoModificado));
    }



}
