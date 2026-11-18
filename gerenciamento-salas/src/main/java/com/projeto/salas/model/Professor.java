package com.projeto.salas.model;

import com.projeto.salas.enums.TipoAula;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "professores")
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nome;

    @Column(nullable = false, unique = true, length = 11)
    private String cpf;

    @Column(nullable = false)
    private String disciplina;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoAula tipoAula;

    @OneToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false, unique = true)
    private Usuario usuario;

    protected Professor() {
    }

    public Professor(String nome, String cpf, String disciplina, TipoAula tipoAula, Usuario usuario) {
        this.nome = nome;
        this.cpf = cpf;
        this.disciplina = disciplina;
        this.tipoAula = tipoAula;
        this.usuario = usuario;
    }


}
