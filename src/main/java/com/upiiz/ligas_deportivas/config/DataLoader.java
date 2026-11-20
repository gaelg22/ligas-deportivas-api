package com.upiiz.ligas_deportivas.config;

import com.upiiz.ligas_deportivas.entities.Liga;
import com.upiiz.ligas_deportivas.entities.Usuario;
import com.upiiz.ligas_deportivas.repositories.LigaRepository;
import com.upiiz.ligas_deportivas.repositories.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;

@Configuration
public class DataLoader implements CommandLineRunner {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final LigaRepository ligaRepository;
    private final String adminUsername;
    private final String adminPassword;

    public DataLoader(UsuarioRepository usuarioRepository,
                      PasswordEncoder passwordEncoder,
                      LigaRepository ligaRepository,
                      @Value("${app.admin.username:gael}") String adminUsername,
                      @Value("${app.admin.password:}") String adminPassword) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.ligaRepository = ligaRepository;
        this.adminUsername = adminUsername;
        this.adminPassword = adminPassword;
    }

    @Override
    public void run(String... args) throws Exception {
        // El admin ya no lleva la contraseña en el código: se toma de APP_ADMIN_PASSWORD
        if (!adminPassword.isBlank() && !usuarioRepository.existsByUsername(adminUsername)) {
            Usuario admin = new Usuario();
            admin.setUsername(adminUsername);
            admin.setPassword(passwordEncoder.encode(adminPassword));
            admin.setRoles(List.of("ADMIN", "USER"));
            usuarioRepository.save(admin);
        }

        if (ligaRepository.count() == 0) {
            Liga l = new Liga("Liga Zacatecana", "Mexico", "2023-2026");
            ligaRepository.save(l);
        }
    }
}
