package com.projeto.salas.controller;

import com.projeto.salas.dto.ApiDtos.*;
import com.projeto.salas.service.SalaService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.*;
import java.util.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/salas")
public class SalaController {

    private final SalaService salas;


    @GetMapping
    public List<SalaView> listar() {
        return salas.listar();
    }

    @GetMapping("/disponibilidade")
    public List<DisponibilidadeView> consultar(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate data,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime inicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.TIME) LocalTime fim) {
        return salas.consultar(data, inicio, fim);
    }
}
