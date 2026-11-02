package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarItemsEntregadosRequest;
import com.felipeparodi.crmbackend.documentacion.service.ActaEntregaItemEntregaService;
import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actas-entrega/{actaEntregaId}/items-entrega")
public class ActaEntregaItemEntregaController {

    private final ActaEntregaItemEntregaService actaEntregaItemEntregaService;

    public ActaEntregaItemEntregaController(ActaEntregaItemEntregaService actaEntregaItemEntregaService) {
        this.actaEntregaItemEntregaService = actaEntregaItemEntregaService;
    }

    @PostMapping
    public List<ItemEntregaResponse> asignar(@PathVariable UUID actaEntregaId,
                                               @RequestBody AsignarItemsEntregadosRequest request) {
        return actaEntregaItemEntregaService.asignar(actaEntregaId, request);
    }

    @GetMapping
    public List<ItemEntregaResponse> listar(@PathVariable UUID actaEntregaId) {
        return actaEntregaItemEntregaService.listarPorActa(actaEntregaId);
    }
}
