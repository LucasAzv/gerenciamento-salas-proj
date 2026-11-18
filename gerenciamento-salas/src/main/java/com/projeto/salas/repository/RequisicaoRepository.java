package com.projeto.salas.repository;

import com.projeto.salas.model.Requisicao;
import com.projeto.salas.enums.StatusRequisicao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

public interface RequisicaoRepository extends JpaRepository<Requisicao, Long> {
    List<Requisicao> findByProfessorUsuarioLoginOrderByDataDescHoraInicioDesc(String login);

    List<Requisicao> findByStatusOrderByDataAscHoraInicioAsc(StatusRequisicao status);

    boolean existsBySalaId(Long salaId);

    @Query("""
             select (count(r) > 0) from Requisicao r
             where r.sala.id = :salaId and r.data = :data and r.status = :status
               and r.horaInicio < :horaFim and r.horaFim > :horaInicio
               and r.id <> :ignorarId
            """)
    boolean existeConflito(@Param("salaId") Long salaId, @Param("data") LocalDate data,
                           @Param("horaInicio") LocalTime horaInicio, @Param("horaFim") LocalTime horaFim,
                           @Param("status") StatusRequisicao status, @Param("ignorarId") Long ignorarId);
}
