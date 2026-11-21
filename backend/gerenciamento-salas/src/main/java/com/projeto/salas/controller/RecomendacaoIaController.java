
package com.projeto.salas.controller;

import com.projeto.salas.dto.RecomendacaoIaDtos.Pedido;
import com.projeto.salas.dto.RecomendacaoIaDtos.Resultado;
import com.projeto.salas.service.RecomendacaoIaService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/recomendacoes")
public class RecomendacaoIaController {

    private final RecomendacaoIaService recomendacaoIaService;

    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping("/ia")
    public Resultado recomendar(
            Authentication authentication,
            @Valid @RequestBody Pedido pedido) {

        return recomendacaoIaService.recomendar(
                authentication.getName(),
                pedido
        );
    }
}
