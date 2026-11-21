package com.projeto.salas.controller;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.service.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin")

@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    private final CadastroService cadastros;
    private final SalaService salas;


    @PostMapping("/professores")
    @ResponseStatus(HttpStatus.CREATED)
    public ProfessorView professor(@Valid @RequestBody ProfessorCreate body) {
        return cadastros.cadastrar(body);
    }

    @PostMapping("/salas")
    @ResponseStatus(HttpStatus.CREATED)
    public SalaView criar(@Valid @RequestBody SalaInput body) {
        return salas.criar(body);
    }

    @PutMapping("/salas/{id}")
    public SalaView atualizar(@PathVariable Long id, @Valid @RequestBody SalaInput body) {
        return salas.atualizar(id, body);
    }

    @PatchMapping("/salas/{id}/disponibilidade")
    public SalaView disponibilidade(@PathVariable Long id, @RequestParam boolean disponivel) {
        return salas.alterarDisponibilidade(id, disponivel);
    }

    @DeleteMapping("/salas/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void excluir(@PathVariable Long id) {
        salas.excluir(id);
    }
}
