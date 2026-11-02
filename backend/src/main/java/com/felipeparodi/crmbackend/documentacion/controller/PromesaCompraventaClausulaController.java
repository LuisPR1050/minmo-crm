package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.service.PromesaCompraventaClausulaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promesas-compraventa/{promesaCompraventaId}/clausulas")
public class PromesaCompraventaClausulaController {

    private final PromesaCompraventaClausulaService promesaCompraventaClausulaService;

    public PromesaCompraventaClausulaController(PromesaCompraventaClausulaService promesaCompraventaClausulaService) {
        this.promesaCompraventaClausulaService = promesaCompraventaClausulaService;
    }

    @PostMapping
    public List<ClausulaPromesaResponse> asignar(@PathVariable UUID promesaCompraventaId,
                                                   @RequestBody AsignarClausulasPromesaRequest request) {
        return promesaCompraventaClausulaService.asignar(promesaCompraventaId, request);
    }

    @GetMapping
    public List<ClausulaPromesaResponse> listar(@PathVariable UUID promesaCompraventaId) {
        return promesaCompraventaClausulaService.listarPorPromesa(promesaCompraventaId);
    }
}
