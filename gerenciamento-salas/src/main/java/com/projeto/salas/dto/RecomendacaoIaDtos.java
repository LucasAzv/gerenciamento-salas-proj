package com.projeto.salas.dto;


import com.projeto.salas.enums.TipoSala;
import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public final class RecomendacaoIaDtos {

    private RecomendacaoIaDtos() {
    }

    // Dados informados pelo professor
    public record Pedido(
            @NotNull
            @FutureOrPresent
            LocalDate data,

            @NotNull
            LocalTime horaInicio,

            @NotNull
            LocalTime horaFim,

            @Min(1)
            Integer quantidadeAlunos
    ) {
    }

    // Sala recomendada pela IA
    public record SalaRecomendada(
            Long salaId,
            TipoSala tipoSala,
            Integer capacidade,
            int posicao,
            String justificativa
    ) {
    }

    // Resposta enviada ao professor
    public record Resultado(
            String disciplina,
            TipoSala tipoSolicitado,
            int totalSalasElegiveis,
            List<SalaRecomendada> recomendacoes
    ) {
    }

    // Formato da resposta do modelo de IA
    public record RespostaModelo(
            List<ItemModelo> recomendacoes
    ) {
    }

    public record ItemModelo(
            Long salaId,
            String justificativa
    ) {
    }
}
