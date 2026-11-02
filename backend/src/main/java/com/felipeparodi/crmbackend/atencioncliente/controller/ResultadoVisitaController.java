package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.ResultadoVisitaRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResultadoVisitaResponse;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResumenVisitaResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.ResultadoVisitaService;
import com.felipeparodi.crmbackend.atencioncliente.service.ResumenVisitaIAService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/resultados-visita")
public class ResultadoVisitaController {

    private final ResultadoVisitaService resultadoVisitaService;
    private final ResumenVisitaIAService resumenVisitaIAService;

    public ResultadoVisitaController(ResultadoVisitaService resultadoVisitaService,
                                      ResumenVisitaIAService resumenVisitaIAService) {
        this.resultadoVisitaService = resultadoVisitaService;
        this.resumenVisitaIAService = resumenVisitaIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ResultadoVisitaResponse crear(@RequestBody ResultadoVisitaRequest request) {
        return resultadoVisitaService.crear(request);
    }

    @GetMapping("/{id}")
    public ResultadoVisitaResponse buscarPorId(@PathVariable UUID id) {
        return resultadoVisitaService.buscarPorId(id);
    }

    @GetMapping("/por-visita/{visitaId}")
    public ResultadoVisitaResponse buscarPorVisita(@PathVariable UUID visitaId) {
        return resultadoVisitaService.buscarPorVisita(visitaId);
    }

    @PostMapping("/{id}/resumen-ia")
    public ResumenVisitaResponse generarResumenConIA(@PathVariable UUID id) {
        return resumenVisitaIAService.generarResumen(id);
    }
}