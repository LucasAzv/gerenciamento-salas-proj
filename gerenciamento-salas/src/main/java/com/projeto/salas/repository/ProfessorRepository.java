package com.projeto.salas.repository;

import com.projeto.salas.model.Professor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProfessorRepository extends JpaRepository<Professor, Long> {
    Optional<Professor> findByUsuarioLogin(String login);

    boolean existsByCpf(String cpf);
}
