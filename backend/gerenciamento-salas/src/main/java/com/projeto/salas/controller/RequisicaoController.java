package com.projeto.salas.controller;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.enums.StatusRequisicao;
import com.projeto.salas.service.RequisicaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/requisicoes")
public class RequisicaoController {

    private final RequisicaoService requisicoes;


    @PreAuthorize("hasRole('PROFESSOR')")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequisicaoView solicitar(Authentication auth, @RequestBody @Valid RequisicaoInput body) {
        return requisicoes.solicitar(auth.getName(), body);
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @GetMapping("/minhas")
    public List<RequisicaoView> minhas(Authentication auth) {
        return requisicoes.minhas(auth.getName());
    }

    @PreAuthorize("hasRole('PROFESSOR')")
    @PatchMapping("/{id}/cancelar")
    public RequisicaoView cancelar(Authentication auth, @PathVariable Long id) {
        return requisicoes.cancelar(auth.getName(), id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping
    public List<RequisicaoView> todas(@RequestParam(required = false) StatusRequisicao status) {
        return requisicoes.listar(status);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/aprovar")
    public RequisicaoView aprovar(@PathVariable Long id) {
        return requisicoes.aprovar(id);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/{id}/rejeitar")
    public RequisicaoView rejeitar(@PathVariable Long id) {
        return requisicoes.rejeitar(id);
    }
}
