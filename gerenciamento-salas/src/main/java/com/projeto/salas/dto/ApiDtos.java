package com.projeto.salas.dto;

import com.projeto.salas.enums.*;
import com.projeto.salas.model.*;
import jakarta.validation.constraints.*;

import java.time.LocalDate;
import java.time.LocalTime;

public final class ApiDtos {
    private ApiDtos() {
    }

    public record LoginRequest(@NotBlank String login, @NotBlank String senha) {
    }

    public record LoginResponse(String token, String tipo, String role, long expiraEmSegundos) {
    }

    public record ProfessorCreate(@NotBlank String nome, @NotBlank String cpf, @NotBlank String disciplina,
                                  @NotNull TipoAula tipoAula, @NotBlank String login, @NotBlank String senha) {
    }

    public record ProfessorView(Long id, String nome, String cpf, String disciplina, TipoAula tipoAula, String login) {
        public static ProfessorView of(Professor p) {
            return new ProfessorView(p.getId(), p.getNome(), p.getCpf(), p.getDisciplina(), p.getTipoAula(), p.getUsuario().getLogin());
        }
    }

    public record SalaInput(@NotNull @Min(1) Integer tamanho, @NotNull TipoSala tipoSala) {
    }

    public record SalaView(Long id, Integer tamanho, TipoSala tipoSala, boolean disponivel) {
        public static SalaView of(Sala s) {
            return new SalaView(s.getId(), s.getTamanho(), s.getTipoSala(), s.isDisponivel());
        }
    }

    public record RequisicaoInput(@NotNull Long salaId, @NotNull @FutureOrPresent LocalDate data,
                                  @NotNull LocalTime horaInicio, @NotNull LocalTime horaFim) {
    }

    public record RequisicaoView(Long id, Long professorId, Long salaId, LocalDate data, LocalTime horaInicio,
                                 LocalTime horaFim, StatusRequisicao status) {
        public static RequisicaoView of(Requisicao r) {
            return new RequisicaoView(r.getId(), r.getProfessor().getId(), r.getSala().getId(), r.getData(), r.getHoraInicio(), r.getHoraFim(), r.getStatus());
        }
    }

    public record DisponibilidadeView(SalaView sala, boolean livreNoHorario) {
    }
}
