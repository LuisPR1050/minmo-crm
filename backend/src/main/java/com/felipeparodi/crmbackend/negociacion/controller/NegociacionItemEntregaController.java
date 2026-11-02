package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.AsignarItemEntregaRequest;
import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import com.felipeparodi.crmbackend.negociacion.service.NegociacionItemEntregaService;
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
@RequestMapping("/api/negociaciones/{negociacionId}/items-entrega")
public class NegociacionItemEntregaController {

    private final NegociacionItemEntregaService negociacionItemEntregaService;

    public NegociacionItemEntregaController(NegociacionItemEntregaService negociacionItemEntregaService) {
        this.negociacionItemEntregaService = negociacionItemEntregaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public void asignar(@PathVariable UUID negociacionId,
                         @RequestBody AsignarItemEntregaRequest request) {
        negociacionItemEntregaService.asignar(negociacionId, request);
    }

    @GetMapping
    public List<ItemEntregaResponse> listarIncluidos(@PathVariable UUID negociacionId) {
        return negociacionItemEntregaService.listarIncluidosPorNegociacion(negociacionId);
    }
}