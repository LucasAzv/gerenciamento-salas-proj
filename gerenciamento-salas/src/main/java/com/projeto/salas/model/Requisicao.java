package com.projeto.salas.model;

import com.projeto.salas.enums.StatusRequisicao;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
@Getter
@Entity
@Table(name = "requisicoes", indexes = {@Index(name = "idx_reserva_sala_data", columnList = "sala_id,data,status")})
public class Requisicao {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "professor_id", nullable = false)
    private Professor professor;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "sala_id", nullable = false)
    private Sala sala;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horaInicio;

    @Column(nullable = false)
    private LocalTime horaFim;

    @Setter
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusRequisicao status = StatusRequisicao.PENDENTE;

    protected Requisicao() {
    }

    public Requisicao(Professor professor, Sala sala, LocalDate data, LocalTime horaInicio, LocalTime horaFim) {
        this.professor = professor;
        this.sala = sala;
        this.data = data;
        this.horaInicio = horaInicio;
        this.horaFim = horaFim;
    }


}
