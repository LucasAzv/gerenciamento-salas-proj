package com.projeto.salas.security;

import com.projeto.salas.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final UsuarioRepository usuarios;



    @Override
    public UserDetails loadUserByUsername(String login) {
        var usuario = usuarios.findByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado"));
        return User.withUsername(usuario.getLogin())
                .password(usuario.getSenha())
                .roles(usuario.getRole().name())
                .build();
    }
}
