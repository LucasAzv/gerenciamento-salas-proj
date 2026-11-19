
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

    List<Requisicao> findByProfessorUsuarioLoginOrderByDataDescHoraInicioDesc(
            String login
    );

    List<Requisicao> findByStatusOrderByDataAscHoraInicioAsc(
            StatusRequisicao status
    );

    boolean existsBySalaId(Long salaId);

    // Verifica conflitos com reservas aprovadas
    @Query("""
            SELECT (COUNT(r) > 0)
            FROM Requisicao r
            WHERE r.sala.id = :salaId
              AND r.data = :data
              AND r.status = :status
              AND r.horaInicio < :horaFim
              AND r.horaFim > :horaInicio
              AND r.id <> :ignorarId
            """)
    boolean existeConflito(
            @Param("salaId") Long salaId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("status") StatusRequisicao status,
            @Param("ignorarId") Long ignorarId
    );

    // Verifica solicitações duplicadas do mesmo professor
    @Query("""
            SELECT (COUNT(r) > 0)
            FROM Requisicao r
            WHERE r.professor.id = :professorId
              AND r.sala.id = :salaId
              AND r.data = :data
              AND r.status IN :statusAtivos
              AND r.horaInicio < :horaFim
              AND r.horaFim > :horaInicio
            """)
    boolean existeSolicitacaoDuplicada(
            @Param("professorId") Long professorId,
            @Param("salaId") Long salaId,
            @Param("data") LocalDate data,
            @Param("horaInicio") LocalTime horaInicio,
            @Param("horaFim") LocalTime horaFim,
            @Param("statusAtivos") List<StatusRequisicao> statusAtivos
    );
}
