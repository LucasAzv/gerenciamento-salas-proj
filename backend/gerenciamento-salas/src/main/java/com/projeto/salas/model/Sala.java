package com.projeto.salas.model;

import com.projeto.salas.enums.TipoSala;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Entity
@Table(name = "salas")
public class Sala {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer tamanho;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private TipoSala tipoSala;

    @Setter
    @Column(nullable = false)
    private boolean disponivel = true;

    protected Sala() {
    }

    public Sala(Integer tamanho, TipoSala tipoSala) {
        this.tamanho = tamanho;
        this.tipoSala = tipoSala;
    }



    public void atualizar(Integer tamanho, TipoSala tipoSala) {
        this.tamanho = tamanho;
        this.tipoSala = tipoSala;
    }
}
