package com.upiiz.ligas_deportivas.security;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class JwtUtilTest {

    private static final String SECRETO = "secreto-de-prueba-con-mas-de-32-caracteres-123";

    private final JwtUtil jwtUtil = new JwtUtil(SECRETO, 60_000);

    @Test
    void tokenGenerado_esValidoYTraeUsuarioYRoles() {
        String token = jwtUtil.generateToken("gael", List.of("ADMIN", "USER"));

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.extractUsername(token)).isEqualTo("gael");
        assertThat(jwtUtil.extractRoles(token)).containsExactly("ADMIN", "USER");
    }

    @Test
    void tokenFirmadoConOtroSecreto_noEsValido() {
        JwtUtil otro = new JwtUtil("otro-secreto-distinto-tambien-de-32-caracteres", 60_000);
        String tokenAjeno = otro.generateToken("intruso", List.of("ADMIN"));

        assertThat(jwtUtil.validateToken(tokenAjeno)).isFalse();
    }

    @Test
    void tokenExpirado_noEsValido() {
        JwtUtil expiraAlInstante = new JwtUtil(SECRETO, -1_000);
        String token = expiraAlInstante.generateToken("gael", List.of("USER"));

        assertThat(jwtUtil.validateToken(token)).isFalse();
    }

    @Test
    void textoCualquiera_noEsValido() {
        assertThat(jwtUtil.validateToken("esto-no-es-un-jwt")).isFalse();
    }
}
