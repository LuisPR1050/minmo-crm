package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.HistorialPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.service.HistorialPublicacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-publicacion")
public class HistorialPublicacionController {

    private final HistorialPublicacionService historialPublicacionService;

    public HistorialPublicacionController(HistorialPublicacionService historialPublicacionService) {
        this.historialPublicacionService = historialPublicacionService;
    }

    @GetMapping
    public List<HistorialPublicacionResponse> listar(@RequestParam UUID publicacionInmuebleId) {
        return historialPublicacionService.listarPorPublicacion(publicacionInmuebleId);
    }
}
