package com.matiasTorres.ecommerce.service;

import com.matiasTorres.ecommerce.dto.UsuarioRequestDTO;
import com.matiasTorres.ecommerce.dto.UsuarioResponseDTO;
import com.matiasTorres.ecommerce.model.Usuario;
import com.matiasTorres.ecommerce.model.enums.Rol;
import com.matiasTorres.ecommerce.repository.UsuarioRepo;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
public class UsuarioServiceImplTest {
    @Mock
    private UsuarioRepo usuarioRepo;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UsuarioServiceImpl usuarioService;


    @Test
    @DisplayName("Se registra un usuario exitosamente")
    void registrarUsuarioExitoso() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("matias@test.com", "password123");
        org.mockito.Mockito.when(usuarioRepo.existsByEmail(dto.mail())).thenReturn(false);
        org.mockito.Mockito.when(passwordEncoder.encode(dto.password())).thenReturn("hash123");

        Usuario usuarioSimulado = new Usuario();
        usuarioSimulado.setIdUsuario(1L);
        usuarioSimulado.setEmail(dto.mail());
        usuarioSimulado.setPasswordHash("hash123");
        usuarioSimulado.setRol(Rol.CUSTOMER);

        org.mockito.Mockito.when(usuarioRepo.save(org.mockito.ArgumentMatchers.any(Usuario.class))).thenReturn(usuarioSimulado);
        UsuarioResponseDTO resultado = usuarioService.registrarUsuario(dto);
        org.junit.jupiter.api.Assertions.assertNotNull(resultado);
        org.junit.jupiter.api.Assertions.assertEquals(1L, resultado.id());
        org.junit.jupiter.api.Assertions.assertEquals("matias@test.com", resultado.mail());
        org.junit.jupiter.api.Assertions.assertEquals(Rol.CUSTOMER, resultado.rol());
    }

    @Test
    @DisplayName("Se intenta registrar un usuario con mail existente y verifica que no se haya guardado")
    void registrarUsuario_EmailDuplicado_LanzaExcepcion() {
        UsuarioRequestDTO dto = new UsuarioRequestDTO("matias@test.com", "password123");
        org.mockito.Mockito.when(usuarioRepo.existsByEmail(dto.mail())).thenReturn(true);

        com.matiasTorres.ecommerce.exception.MailYaExisteException excepcion =
                org.junit.jupiter.api.Assertions.assertThrows(
                        com.matiasTorres.ecommerce.exception.MailYaExisteException.class,
                        () -> usuarioService.registrarUsuario(dto)
                );
        org.junit.jupiter.api.Assertions.assertEquals("El mail ya está registrado", excepcion.getMessage());

        //  verifica que nunca intento guardar en la base de datos
        org.mockito.Mockito.verify(usuarioRepo, org.mockito.Mockito.never())
                .save(org.mockito.ArgumentMatchers.any(Usuario.class));
    }

    @Test
    @DisplayName("Se intenta iniciar sesion con mail existente")
    void loadUserByEmailExistente(){
        Usuario usuario = new Usuario();
        usuario.setEmail("matias@test.com");
        usuario.setPasswordHash("hash123");
        usuario.setRol(Rol.CUSTOMER);

        org.mockito.Mockito.when(usuarioRepo.findByEmail("matias@test.com")).thenReturn(java.util.Optional.of(usuario));
        org.springframework.security.core.userdetails.UserDetails userDetails = usuarioService.loadUserByUsername("matias@test.com");

        org.junit.jupiter.api.Assertions.assertNotNull(userDetails);
        org.junit.jupiter.api.Assertions.assertEquals("matias@test.com", userDetails.getUsername());
        org.junit.jupiter.api.Assertions.assertTrue(
                userDetails.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_CUSTOMER"))
        );
    }

    @Test
    @DisplayName("Se intenta iniciar sesion con mail inexistente")
    void loadUserByEmailInexistente(){
        org.mockito.Mockito.when(usuarioRepo.findByEmail("inexistente@test.com"))
                .thenReturn(java.util.Optional.empty());
        org.junit.jupiter.api.Assertions.assertThrows(
                org.springframework.security.core.userdetails.UsernameNotFoundException.class,
                () -> usuarioService.loadUserByUsername("inexistente@test.com")
        );
    }
}
