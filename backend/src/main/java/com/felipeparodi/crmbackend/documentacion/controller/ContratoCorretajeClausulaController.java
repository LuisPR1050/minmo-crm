package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.ContratoCorretajeClausulaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contratos-corretaje/{contratoCorretajeId}/clausulas")
public class ContratoCorretajeClausulaController {

    private final ContratoCorretajeClausulaService contratoCorretajeClausulaService;

    public ContratoCorretajeClausulaController(ContratoCorretajeClausulaService contratoCorretajeClausulaService) {
        this.contratoCorretajeClausulaService = contratoCorretajeClausulaService;
    }

    @PostMapping
    public List<ClausulaCorretajeResponse> asignar(@PathVariable UUID contratoCorretajeId,
                                                     @RequestBody AsignarClausulasCorretajeRequest request) {
        return contratoCorretajeClausulaService.asignar(contratoCorretajeId, request);
    }

    @GetMapping
    public List<ClausulaCorretajeResponse> listar(@PathVariable UUID contratoCorretajeId) {
        return contratoCorretajeClausulaService.listarPorContrato(contratoCorretajeId);
    }
}
