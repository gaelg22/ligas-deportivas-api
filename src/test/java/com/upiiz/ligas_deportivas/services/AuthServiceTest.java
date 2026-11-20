package com.upiiz.ligas_deportivas.services;

import com.upiiz.ligas_deportivas.dto.AuthRequest;
import com.upiiz.ligas_deportivas.entities.Usuario;
import com.upiiz.ligas_deportivas.repositories.UsuarioRepository;
import com.upiiz.ligas_deportivas.security.JwtUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private AuthenticationManager authenticationManager;

    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthService authService;

    @Test
    void register_guardaLaContrasenaEncriptadaYConRolUser() {
        when(usuarioRepository.existsByUsername("ana")).thenReturn(false);
        when(passwordEncoder.encode("secreta")).thenReturn("hash-de-secreta");

        authService.register("ana", "secreta");

        ArgumentCaptor<Usuario> captor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).save(captor.capture());
        Usuario guardado = captor.getValue();
        assertThat(guardado.getPassword()).isEqualTo("hash-de-secreta");
        assertThat(guardado.getRoles()).containsExactly("USER");
    }

    @Test
    void register_conUsuarioRepetido_lanzaExcepcion() {
        when(usuarioRepository.existsByUsername("ana")).thenReturn(true);

        assertThatThrownBy(() -> authService.register("ana", "secreta"))
                .hasMessage("El usuario ya existe");
        verify(usuarioRepository, never()).save(any());
    }

    @Test
    void login_conCredencialesCorrectas_regresaToken() {
        Usuario ana = new Usuario("ana", "hash", List.of("USER"));
        when(usuarioRepository.findByUsername("ana")).thenReturn(Optional.of(ana));
        when(jwtUtil.generateToken("ana", List.of("USER"))).thenReturn("token-falso");

        String token = authService.login(peticion("ana", "secreta"));

        assertThat(token).isEqualTo("token-falso");
    }

    @Test
    void login_conContrasenaIncorrecta_noGeneraToken() {
        when(authenticationManager.authenticate(any(UsernamePasswordAuthenticationToken.class)))
                .thenThrow(new BadCredentialsException("Credenciales incorrectas"));

        assertThatThrownBy(() -> authService.login(peticion("ana", "mala")))
                .isInstanceOf(BadCredentialsException.class);
        verify(jwtUtil, never()).generateToken(any(), any());
    }

    private AuthRequest peticion(String usuario, String contrasena) {
        AuthRequest request = new AuthRequest();
        request.setUsername(usuario);
        request.setPassword(contrasena);
        return request;
    }
}
