package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.HistorialNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.service.HistorialNegociacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-negociacion")
public class HistorialNegociacionController {

    private final HistorialNegociacionService historialNegociacionService;

    public HistorialNegociacionController(HistorialNegociacionService historialNegociacionService) {
        this.historialNegociacionService = historialNegociacionService;
    }

    @GetMapping
    public List<HistorialNegociacionResponse> listar(@RequestParam UUID negociacionId) {
        return historialNegociacionService.listarPorNegociacion(negociacionId);
    }
}
