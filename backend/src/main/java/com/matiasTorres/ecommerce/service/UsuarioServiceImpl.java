package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.UsuarioRequestDTO;
import com.matiasTorres.ecommerce.dto.UsuarioResponseDTO;
import com.matiasTorres.ecommerce.exception.MailYaExisteException;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.repository.UsuarioRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import static com.matiasTorres.ecommerce.model.enums.Rol.CUSTOMER;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {
    private final UsuarioRepo usuarioRepo;
    private final PasswordEncoder passwordEncoder;


    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado"));
    }

    @Override
    public UsuarioResponseDTO registrarUsuario(UsuarioRequestDTO dto){
        if (usuarioRepo.existsByEmail(dto.mail())) {
            throw new MailYaExisteException("El mail ya está registrado");
        }
        Usuario us = new Usuario();
        us.setEmail(dto.mail());
        us.setPasswordHash(
                passwordEncoder.encode(dto.password())
        );
        us.setRol(CUSTOMER);
        Usuario guardado = usuarioRepo.save(us);
        return new UsuarioResponseDTO(guardado.getIdUsuario(),guardado.getEmail(), guardado.getRol());
    }
}
