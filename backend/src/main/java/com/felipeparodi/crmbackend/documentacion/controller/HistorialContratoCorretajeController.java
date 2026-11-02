package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.HistorialContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.HistorialContratoCorretajeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/historial-contrato-corretaje")
public class HistorialContratoCorretajeController {

    private final HistorialContratoCorretajeService historialContratoCorretajeService;

    public HistorialContratoCorretajeController(HistorialContratoCorretajeService historialContratoCorretajeService) {
        this.historialContratoCorretajeService = historialContratoCorretajeService;
    }

    @GetMapping
    public List<HistorialContratoCorretajeResponse> listar(@RequestParam UUID contratoCorretajeId) {
        return historialContratoCorretajeService.listarPorContrato(contratoCorretajeId);
    }
}
