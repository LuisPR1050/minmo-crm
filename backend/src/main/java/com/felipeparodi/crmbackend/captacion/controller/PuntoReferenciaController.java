package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.PuntoReferenciaResponse;
import com.felipeparodi.crmbackend.captacion.service.PuntoReferenciaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/puntos-referencia")
public class PuntoReferenciaController {

    private final PuntoReferenciaService puntoReferenciaService;

    public PuntoReferenciaController(PuntoReferenciaService puntoReferenciaService) {
        this.puntoReferenciaService = puntoReferenciaService;
    }

    @GetMapping
    public List<PuntoReferenciaResponse> listar() {
        return puntoReferenciaService.listarTodas();
    }
}
