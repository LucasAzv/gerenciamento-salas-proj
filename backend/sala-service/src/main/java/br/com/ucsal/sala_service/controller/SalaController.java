package br.com.ucsal.sala_service.controller;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import br.com.ucsal.sala_service.dtos.ErroResponseDTO;
import br.com.ucsal.sala_service.dtos.SalaRequestDTO;
import br.com.ucsal.sala_service.dtos.SalaResponseDTO;
import br.com.ucsal.sala_service.enums.EstadoSala;
import br.com.ucsal.sala_service.enums.TipoSala;
import br.com.ucsal.sala_service.service.SalaService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/salas")
@RequiredArgsConstructor
public class SalaController {

    private final SalaService salaService;

    @GetMapping
    public ResponseEntity<List<SalaResponseDTO>> listarSalas() {
        return ResponseEntity.ok(salaService.listarSalas());
    }

    @GetMapping("/filtro")
    public ResponseEntity<List<SalaResponseDTO>> filtrarSalas(
            @RequestParam(required = false) TipoSala tipo,
            @RequestParam(required = false) @Min(1) Integer capacidade,
            @RequestParam(required = false) EstadoSala estado) {
        return ResponseEntity.ok(salaService.filtrarSalas(tipo, capacidade, estado));
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable UUID id) {
        if (!salaService.existePorId(id)) {
            return salaNaoEncontrada(id);
        }
        return ResponseEntity.ok(salaService.buscarPorId(id));
    }

    @PostMapping
    public ResponseEntity<?> cadastrar(@RequestBody @Valid SalaRequestDTO salaRequestDTO) {
        if (salaService.existePorNome(salaRequestDTO.nome())) {
            return nomeDuplicado(salaRequestDTO.nome());
        }

        SalaResponseDTO salaCriada = salaService.cadastrar(salaRequestDTO);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(salaCriada.id())
                .toUri();
        return ResponseEntity.created(location).body(salaCriada);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> atualizar(@PathVariable UUID id,
                                       @RequestBody @Valid SalaRequestDTO salaRequestDTO) {
        if (!salaService.existePorId(id)) {
            return salaNaoEncontrada(id);
        }
        if (salaService.existeOutraSalaComNome(salaRequestDTO.nome(), id)) {
            return nomeDuplicado(salaRequestDTO.nome());
        }
        return ResponseEntity.ok(salaService.atualizar(id, salaRequestDTO));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deletar(@PathVariable UUID id) {
        if (!salaService.existePorId(id)) {
            return salaNaoEncontrada(id);
        }
        salaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    private ResponseEntity<ErroResponseDTO> salaNaoEncontrada(UUID id) {
        return erro(HttpStatus.NOT_FOUND, "Sala com id " + id + " não encontrada.");
    }

    private ResponseEntity<ErroResponseDTO> nomeDuplicado(String nome) {
        return erro(HttpStatus.CONFLICT, "Já existe uma sala cadastrada com o nome '" + nome.trim() + "'.");
    }

    private ResponseEntity<ErroResponseDTO> erro(HttpStatus status, String mensagem) {
        ErroResponseDTO corpo = new ErroResponseDTO(
                LocalDateTime.now(),
                status.value(),
                status.getReasonPhrase(),
                mensagem);
        return ResponseEntity.status(status).body(corpo);
    }
}
