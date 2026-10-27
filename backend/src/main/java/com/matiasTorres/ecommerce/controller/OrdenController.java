package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.OrdenDetalleResponseDTO;
import com.matiasTorres.ecommerce.dto.OrdenResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.service.OrdenService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/ordenes")
public class OrdenController {

    private final OrdenService ordenService;

    @GetMapping
    public ResponseEntity<List<OrdenResponseDTO>> verOrdenes(@AuthenticationPrincipal Usuario usuario){
        return ResponseEntity.ok(ordenService.verOrdenesUsuario(usuario));
    }

    @GetMapping("/{idOrden}")
    public ResponseEntity<OrdenDetalleResponseDTO> verOrdenDetalle(@AuthenticationPrincipal Usuario usuario, @PathVariable Long idOrden){
        return ResponseEntity.ok(ordenService.verOrdenDetalle(usuario,idOrden));
    }

}
