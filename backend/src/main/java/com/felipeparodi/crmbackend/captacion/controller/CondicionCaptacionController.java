package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.CondicionCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.CondicionCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.service.CondicionCaptacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/condiciones-captacion")
public class CondicionCaptacionController {

    private final CondicionCaptacionService condicionCaptacionService;

    public CondicionCaptacionController(CondicionCaptacionService condicionCaptacionService) {
        this.condicionCaptacionService = condicionCaptacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public CondicionCaptacionResponse crear(@RequestBody CondicionCaptacionRequest request) {
        return condicionCaptacionService.crear(request);
    }

    @GetMapping
    public List<CondicionCaptacionResponse> listarTodos() {
        return condicionCaptacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public CondicionCaptacionResponse buscarPorId(@PathVariable UUID id) {
        return condicionCaptacionService.buscarPorId(id);
    }

    @GetMapping("/por-inmueble/{inmuebleId}")
    public CondicionCaptacionResponse buscarPorInmueble(@PathVariable UUID inmuebleId) {
        return condicionCaptacionService.buscarPorInmueble(inmuebleId);
    }
}