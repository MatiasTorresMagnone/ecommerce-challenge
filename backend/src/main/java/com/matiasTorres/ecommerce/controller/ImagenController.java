package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.storage.StorageService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/imagenes")
public class ImagenController {

    private final StorageService storageService;

    @GetMapping("/{nombre}")
    public ResponseEntity<Resource> ver(@PathVariable String nombre) {
        Resource recurso = storageService.cargar(nombre);
        MediaType tipo = MediaTypeFactory.getMediaType(recurso)
                .orElse(MediaType.APPLICATION_OCTET_STREAM);

        return ResponseEntity.ok()
                .contentType(tipo)
                .header(HttpHeaders.CACHE_CONTROL, "public, max-age=86400")
                .header("X-Content-Type-Options", "nosniff")
                .body(recurso);
    }
}