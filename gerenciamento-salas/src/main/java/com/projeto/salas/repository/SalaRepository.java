package com.projeto.salas.repository;

import com.projeto.salas.model.Sala;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import jakarta.persistence.LockModeType;

import java.util.Optional;

public interface SalaRepository extends JpaRepository<Sala, Long> {

 @Lock(LockModeType.PESSIMISTIC_WRITE)
 Optional<Sala> findSalaById(Long id);
}