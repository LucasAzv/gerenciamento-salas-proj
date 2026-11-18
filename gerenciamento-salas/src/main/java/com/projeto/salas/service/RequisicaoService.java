package com.projeto.salas.service;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.enums.StatusRequisicao;
import com.projeto.salas.model.*;
import com.projeto.salas.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Service
@RequiredArgsConstructor
public class RequisicaoService {
    private final RequisicaoRepository requisicoes;
    private final SalaRepository salas;
    private final ProfessorRepository professores;

    @PersistenceContext
    private EntityManager entityManager;


    @Transactional
    public RequisicaoView solicitar(String login, RequisicaoInput dto) {
        SalaService.validarHorario(dto.data(), dto.horaInicio(), dto.horaFim());
        var professor = professores.findByUsuarioLogin(login).orElseThrow(() -> new ResponseStatusException(HttpStatus.FORBIDDEN, "Professor não associado ao usuário"));

        var sala = salas.findSalaById(dto.salaId()).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));

        if (!sala.isDisponivel())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala bloqueada pelo administrador");

        if (requisicoes.existeConflito(sala.getId(), dto.data(), dto.horaInicio(), dto.horaFim(), StatusRequisicao.APROVADA, -1L))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala já reservada nesse horário");

        return RequisicaoView.of(requisicoes.save(new Requisicao(professor, sala, dto.data(), dto.horaInicio(), dto.horaFim())));
    }

    @Transactional
    public RequisicaoView aprovar(Long id) {

        var pre = buscarRequisicao(id);

        var sala = bloquearSala(pre.getSala().getId());

        var r = pre;
        entityManager.refresh(r);
        if (r.getStatus() != StatusRequisicao.PENDENTE)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisição não está pendente");

        SalaService.validarHorario(r.getData(), r.getHoraInicio(), r.getHoraFim());
        if (!sala.isDisponivel())
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Sala bloqueada pelo administrador");

        if (requisicoes.existeConflito(sala.getId(), r.getData(), r.getHoraInicio(), r.getHoraFim(), StatusRequisicao.APROVADA, r.getId()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Já existe reserva aprovada conflitante");
        r.setStatus(StatusRequisicao.APROVADA);

        return RequisicaoView.of(r);
    }

    @Transactional
    public RequisicaoView rejeitar(Long id) {
        var pre = buscarRequisicao(id);
        bloquearSala(pre.getSala().getId());
        var r = pre;
        entityManager.refresh(r);

        if (r.getStatus() != StatusRequisicao.PENDENTE)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Requisição não está pendente");
        r.setStatus(StatusRequisicao.REJEITADA);
        return RequisicaoView.of(r);
    }

    @Transactional
    public RequisicaoView cancelar(String login, Long id) {
        var pre = buscarRequisicao(id);
        bloquearSala(pre.getSala().getId());
        var r = pre;
        entityManager.refresh(r);
        if (!r.getProfessor().getUsuario().getLogin().equals(login))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Requisição não pertence ao professor");

        if (r.getStatus() != StatusRequisicao.PENDENTE && r.getStatus() != StatusRequisicao.APROVADA)
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Não é possível cancelar esta requisição");
        r.setStatus(StatusRequisicao.CANCELADA);
        return RequisicaoView.of(r);
    }

    private Requisicao buscarRequisicao(Long id) {
        return requisicoes.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Requisição não encontrada"));
    }

    private Sala bloquearSala(Long id) {
        return salas.findSalaById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Sala não encontrada"));
    }

    @Transactional(readOnly = true)
    public List<RequisicaoView> minhas(String login) {
        return requisicoes.findByProfessorUsuarioLoginOrderByDataDescHoraInicioDesc(login).stream().map(RequisicaoView::of).toList();
    }

    @Transactional(readOnly = true)
    public List<RequisicaoView> listar(StatusRequisicao status) {
        return (status == null ? requisicoes.findAll() : requisicoes.findByStatusOrderByDataAscHoraInicioAsc(status)).stream().map(RequisicaoView::of).toList();
    }
}
