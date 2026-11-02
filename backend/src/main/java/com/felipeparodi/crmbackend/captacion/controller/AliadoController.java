package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.AliadoRequest;
import com.felipeparodi.crmbackend.captacion.dto.AliadoResponse;
import com.felipeparodi.crmbackend.captacion.service.AliadoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/aliados")
public class AliadoController {

    private final AliadoService aliadoService;

    public AliadoController(AliadoService aliadoService) {
        this.aliadoService = aliadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public AliadoResponse crear(@RequestBody AliadoRequest request) {
        return aliadoService.crear(request);
    }

    @GetMapping
    public List<AliadoResponse> listar() {
        return aliadoService.listarTodos();
    }
}
