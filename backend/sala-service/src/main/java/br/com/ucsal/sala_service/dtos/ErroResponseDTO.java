package br.com.ucsal.sala_service.dtos;

import java.time.LocalDateTime;

public record ErroResponseDTO(
        LocalDateTime timestamp,
        int status,
        String erro,
        String mensagem) {
}
