package com.projeto.salas.service;

import com.projeto.salas.model.Usuario;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.Date;

@Service
public class JwtService {
    private final Key key;
    private static final long EXPIRACAO_MS = 1000L * 60 * 60;

    public JwtService(@Value("${app.jwt.secret}") String secret) {

        this.key = Keys.hmacShaKeyFor(
                secret.getBytes(StandardCharsets.UTF_8)
        );
    }

    public String emitir(Usuario usuario) {
        Date agora = new Date();
        return Jwts.builder()
                .setSubject(usuario.getLogin())
                .claim("role", usuario.getRole().name())
                .setIssuedAt(agora)
                .setExpiration(new Date(agora.getTime() + EXPIRACAO_MS))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extrairUsername(String token) {
        var jws = parser().parseClaimsJws(token);
        if (!"HS256".equals(jws.getHeader().getAlgorithm())) {
            throw new JwtException("Algoritmo JWT inválido");
        }
        Claims claims = jws.getBody();
        if (claims.getExpiration() == null || claims.getSubject() == null
                || claims.getSubject().isBlank()) {
            throw new JwtException("Token incompleto");
        }
        return claims.getSubject();
    }

    private JwtParser parser() {
        return Jwts.parserBuilder().setSigningKey(key).build();
    }

    public long expiraEmSegundos() {
        return EXPIRACAO_MS / 1000;
    }
}
