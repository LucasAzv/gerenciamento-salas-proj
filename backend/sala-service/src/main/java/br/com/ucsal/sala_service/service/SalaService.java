package br.com.ucsal.sala_service.service;

import java.util.List;
import java.util.UUID;

import br.com.ucsal.sala_service.dtos.SalaRequestDTO;
import br.com.ucsal.sala_service.dtos.SalaResponseDTO;
import br.com.ucsal.sala_service.enums.EstadoSala;
import br.com.ucsal.sala_service.enums.TipoSala;
import br.com.ucsal.sala_service.mapper.SalaMapper;

import org.springframework.stereotype.Service;


import br.com.ucsal.sala_service.model.Sala;
import br.com.ucsal.sala_service.repository.SalaRepository;
import br.com.ucsal.sala_service.specification.SalaSpecification;
import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class SalaService {

    private final SalaRepository salaRepository;
    private final SalaMapper salaMapper;

    private Sala buscarEntidade(UUID uuid){
        return salaRepository.findById(uuid)
                .orElseThrow(() -> new RuntimeException("Sala com id " + uuid + " não encontrada"));
    }

    @Transactional(readOnly = true)
    public boolean existePorId(UUID uuid){
        return salaRepository.existsById(uuid);
    }

    @Transactional(readOnly = true)
    public boolean existePorNome(String nome){
        return salaRepository.existsByNomeIgnoreCase(nome.trim());
    }

    @Transactional(readOnly = true)
    public boolean existeOutraSalaComNome(String nome, UUID uuid){
        return salaRepository.existsByNomeIgnoreCaseAndIdNot(nome.trim(), uuid);
    }

    @Transactional(readOnly = true)
    public List<SalaResponseDTO> listarSalas(){
       List<Sala> salas = salaRepository.findAll();
       return salaMapper.toResponseDTOList(salas);
    }
    @Transactional(readOnly = true)
    public List<SalaResponseDTO> filtrarSalas(TipoSala tipo, Integer capacidade, EstadoSala estado){
        List<Sala> salas = salaRepository.findAll(SalaSpecification.filtrar(tipo, capacidade, estado));
        return salaMapper.toResponseDTOList(salas);
    }
    @Transactional(readOnly = true)
    public SalaResponseDTO buscarPorId(UUID uuid){
        Sala sala = buscarEntidade(uuid);
        return salaMapper.toResponseDTO(sala);
    }
    @Transactional
    public SalaResponseDTO cadastrar(SalaRequestDTO salaRequestDTO){
        Sala sala = salaMapper.toEntity(salaRequestDTO);
        sala.setEstadoSala(EstadoSala.DISPONIVEL);

        Sala salvarSala = salaRepository.save(sala);
        return salaMapper.toResponseDTO(salvarSala);
    }
    @Transactional
    public SalaResponseDTO atualizar(UUID uuid, SalaRequestDTO salaRequestDTO){
        Sala sala = buscarEntidade(uuid);
        salaMapper.updateEntityFromDto(salaRequestDTO,sala);

        Sala salaAtualizada = salaRepository.save(sala);
        return salaMapper.toResponseDTO(salaAtualizada);
    }
    @Transactional
    public void deletar(UUID uuid){
       Sala sala = buscarEntidade(uuid);
        salaRepository.delete(sala);
    }

}
