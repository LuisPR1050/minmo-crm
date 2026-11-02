package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.dto.RevisionActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.service.ActaEntregaService;
import com.felipeparodi.crmbackend.documentacion.service.RevisionActaEntregaIAService;
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
@RequestMapping("/api/actas-entrega")
public class ActaEntregaController {

    private final ActaEntregaService actaEntregaService;
    private final RevisionActaEntregaIAService revisionActaEntregaIAService;

    public ActaEntregaController(ActaEntregaService actaEntregaService,
                                  RevisionActaEntregaIAService revisionActaEntregaIAService) {
        this.actaEntregaService = actaEntregaService;
        this.revisionActaEntregaIAService = revisionActaEntregaIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ActaEntregaResponse crear(@jakarta.validation.Valid @RequestBody ActaEntregaRequest request) {
        return actaEntregaService.crear(request);
    }

    @GetMapping
    public List<ActaEntregaResponse> listarTodas() {
        return actaEntregaService.listarTodas();
    }

    @GetMapping("/{id}")
    public ActaEntregaResponse buscarPorId(@PathVariable UUID id) {
        return actaEntregaService.buscarPorId(id);
    }

    @GetMapping("/negociacion/{negociacionId}")
    public ActaEntregaResponse buscarPorNegociacion(@PathVariable UUID negociacionId) {
        return actaEntregaService.buscarPorNegociacion(negociacionId);
    }

    @PostMapping("/{id}/revisar-ia")
    public RevisionActaEntregaResponse revisarIA(@PathVariable UUID id) {
        return revisionActaEntregaIAService.revisar(id);
    }
}
