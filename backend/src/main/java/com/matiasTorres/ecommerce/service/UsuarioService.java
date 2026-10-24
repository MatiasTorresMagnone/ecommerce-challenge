package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.UsuarioRequestDTO;
import com.matiasTorres.ecommerce.dto.UsuarioResponseDTO;

public interface UsuarioService {
    UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto);
}
