package com.projeto.salas.service;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.enums.Role;
import com.projeto.salas.model.*;
import lombok.RequiredArgsConstructor;
import com.projeto.salas.repository.*;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class CadastroService {
    private final UsuarioRepository usuarios;
    private final ProfessorRepository professores;
    private final PasswordEncoder encoder;


    @Transactional
    public ProfessorView cadastrar(ProfessorCreate dto) {
        String cpf = dto.cpf().replaceAll("[^0-9]", "");
        if (!cpfValido(cpf)) throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "CPF inválido");

        if (dto.senha().length() < 8)
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Senha deve ter ao menos 8 caracteres");

        if (usuarios.existsByLogin(dto.login()))
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Login já existe");

        if (professores.existsByCpf(cpf)) throw new ResponseStatusException(HttpStatus.CONFLICT, "CPF já existe");

        var user = usuarios.save(new Usuario(dto.login(), encoder.encode(dto.senha()), Role.PROFESSOR));
        var professor = professores.save(new Professor(dto.nome(), cpf, dto.disciplina(), dto.tipoAula(), user));

        return ProfessorView.of(professor);
    }

    private boolean cpfValido(String cpf) {
        if (cpf.length() != 11 || cpf.chars().distinct().count() == 1) return false;

        for (int etapa = 9; etapa <= 10; etapa++) {
            int soma = 0;

            for (int i = 0; i < etapa; i++) soma += (cpf.charAt(i) - '0') * (etapa + 1 - i);

            int digito = (soma * 10) % 11;

            if (digito == 10) digito = 0;

            if (digito != cpf.charAt(etapa) - '0') return false;
        }
        return true;
    }
}
