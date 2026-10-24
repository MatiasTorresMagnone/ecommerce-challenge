package com.matiasTorres.ecommerce.controller;

import com.matiasTorres.ecommerce.dto.LoginRequestDTO;
import com.matiasTorres.ecommerce.dto.UsuarioRequestDTO;
import com.matiasTorres.ecommerce.dto.UsuarioResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.security.DatosJWTToken;
import com.matiasTorres.ecommerce.security.TokenService;
import com.matiasTorres.ecommerce.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.authentication.AuthenticationManager;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final TokenService tokenService;
    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public ResponseEntity<DatosJWTToken> login(@RequestBody LoginRequestDTO dto){
        var authToken = new UsernamePasswordAuthenticationToken(dto.mail(),dto.password());
        var usuarioAutenticado = authenticationManager.authenticate(authToken);
        var JWTtoken = tokenService.generarToken((Usuario) usuarioAutenticado.getPrincipal());
        return ResponseEntity.ok(new DatosJWTToken(JWTtoken));
    }

    @PostMapping("/register")
    public ResponseEntity<UsuarioResponseDTO> saveUsuario(@RequestBody UsuarioRequestDTO dto){
        UsuarioResponseDTO us = usuarioService.registrarUsuario(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(us);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(){
        return ResponseEntity.ok("Sesión cerrada exitosamente");
    }
}
