package com.projeto.salas.controller;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.repository.UsuarioRepository;
import com.projeto.salas.service.JwtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/auth")

public class AuthController {
    private final AuthenticationManager authentication;
    private final UsuarioRepository usuarios;
    private final JwtService tokens;


    @PostMapping("/login")
    public LoginResponse login(@RequestBody @Valid LoginRequest request) {
        try {
            authentication.authenticate(new UsernamePasswordAuthenticationToken(request.login(), request.senha()));
        } catch (org.springframework.security.core.AuthenticationException ex) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas");
        }
        var usuario = usuarios.findByLogin(request.login()).orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciais inválidas"));
        return new LoginResponse(tokens.emitir(usuario), "Bearer", usuario.getRole().name(), tokens.expiraEmSegundos());
    }
}
