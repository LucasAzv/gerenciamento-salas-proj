package com.projeto.salas.config;

import com.projeto.salas.enums.Role;
import com.projeto.salas.model.Usuario;
import com.projeto.salas.repository.UsuarioRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AdminBootstrap {
    @Bean
    ApplicationRunner criarAdminInicial(UsuarioRepository usuarios, PasswordEncoder encoder,
                                        @Value("${ADMIN_LOGIN:}") String login, @Value("${ADMIN_PASSWORD:}") String senha) {
        return args -> {
            if (login.isBlank() || senha.isBlank()) return;

            if (senha.length() < 12)
                throw new IllegalArgumentException("ADMIN_PASSWORD deve ter pelo menos 12 caracteres");

            if (!usuarios.existsByLogin(login)) usuarios.save(new Usuario(login, encoder.encode(senha), Role.ADMIN));
        };
    }
}
