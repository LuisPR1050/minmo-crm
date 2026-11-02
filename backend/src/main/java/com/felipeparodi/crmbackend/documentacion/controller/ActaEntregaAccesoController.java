package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AccesoEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.dto.AsignarAccesosEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.service.ActaEntregaAccesoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actas-entrega/{actaEntregaId}/accesos")
public class ActaEntregaAccesoController {

    private final ActaEntregaAccesoService actaEntregaAccesoService;

    public ActaEntregaAccesoController(ActaEntregaAccesoService actaEntregaAccesoService) {
        this.actaEntregaAccesoService = actaEntregaAccesoService;
    }

    @PostMapping
    public List<AccesoEntregaResponse> asignar(@PathVariable UUID actaEntregaId,
                                                 @RequestBody AsignarAccesosEntregaRequest request) {
        return actaEntregaAccesoService.asignar(actaEntregaId, request);
    }

    @GetMapping
    public List<AccesoEntregaResponse> listar(@PathVariable UUID actaEntregaId) {
        return actaEntregaAccesoService.listarPorActa(actaEntregaId);
    }
}
