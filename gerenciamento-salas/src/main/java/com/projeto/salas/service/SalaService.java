package com.projeto.salas.service;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.enums.StatusRequisicao;
import com.projeto.salas.model.Sala;
import com.projeto.salas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.*;
import java.util.*;

@Service
@RequiredArgsConstructor
public class SalaService {
    private final SalaRepository salas;
    private final RequisicaoRepository requisicoes;

    

    @Transactional(readOnly = true)
    public List<SalaView> listar() {
        return salas.findAll().stream().map(SalaView::of).toList();
    }

    @Transactional(readOnly = true)
    public List<DisponibilidadeView> consultar(LocalDate data, LocalTime inicio, LocalTime fim) {
        validarHorario(data, inicio, fim);
        return salas.findAll().stream().map(s -> new DisponibilidadeView(SalaView.of(s), s.isDisponivel() && !requisicoes.existeConflito(s.getId(), data, inicio, fim, StatusRequisicao.APROVADA, -1L))).toList();
    }

    @Transactional
    public SalaView criar(SalaInput input) {
        return SalaView.of(salas.save(new Sala(input.tamanho(), input.tipoSala())));
    }

    @Transactional
    public SalaView atualizar(Long id, SalaInput input) {
        Sala s = encontrar(id);
        s.atualizar(input.tamanho(), input.tipoSala());
        return SalaView.of(s);
    }

    @Transactional
    public SalaView alterarDisponibilidade(Long id, boolean disponivel) {
        Sala s = encontrar(id);
        s.setDisponivel(disponivel);
        return SalaView.of(s);
    }

    @Transactional
    public void excluir(Long id) {
        Sala s = encontrar(id);
        if (requisicoes.existsBySalaId(id))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala possui histórico de requisições; bloqueie-a em vez de excluir");
        salas.delete(s);
    }

    private Sala encontrar(Long id) {
        return salas.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
    }

    public static void validarHorario(LocalDate data, LocalTime inicio, LocalTime fim) {
        if (!inicio.isBefore(fim))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Horário inicial deve ser anterior ao final");

        if (data.isBefore(LocalDate.now()) || (data.equals(LocalDate.now()) && !inicio.isAfter(LocalTime.now())))
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "O início da reserva precisa estar no futuro");
    }
}
