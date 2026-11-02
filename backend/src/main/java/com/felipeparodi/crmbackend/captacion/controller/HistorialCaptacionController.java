package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.HistorialCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.HistorialCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.service.HistorialCaptacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-captacion")
public class HistorialCaptacionController {

    private final HistorialCaptacionService historialCaptacionService;

    public HistorialCaptacionController(HistorialCaptacionService historialCaptacionService) {
        this.historialCaptacionService = historialCaptacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public HistorialCaptacionResponse crear(@RequestBody HistorialCaptacionRequest request) {
        return historialCaptacionService.crear(request);
    }

    @GetMapping
    public List<HistorialCaptacionResponse> listar(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return historialCaptacionService.listarPorInmueble(inmuebleId);
        }
        return historialCaptacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public HistorialCaptacionResponse buscarPorId(@PathVariable UUID id) {
        return historialCaptacionService.buscarPorId(id);
    }
}