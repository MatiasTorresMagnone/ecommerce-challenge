package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.ProductoResponseDTO;
import com.matiasTorres.ecommerce.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/productos")
public class ProductoController {

    private final ProductoService productoService;

    @GetMapping
    public ResponseEntity<List<ProductoResponseDTO>> listarProductosActivos(){
        List<ProductoResponseDTO> productos = productoService.findAllActivas();
        return ResponseEntity.ok(productos);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDTO> buscarProductoPorId(@PathVariable Long id){
        ProductoResponseDTO producto = productoService.findById(id);
        return ResponseEntity.ok(producto);
    }

    @GetMapping("/buscar")
    public ResponseEntity<List<ProductoResponseDTO>> buscarProductoPorNombre(@RequestParam String nombre){
        List<ProductoResponseDTO> productos = productoService.findByNombre(nombre);
        return ResponseEntity.ok(productos);
    }

}
