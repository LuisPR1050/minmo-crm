package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarCondicionesEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CondicionEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.service.ActaEntregaCondicionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actas-entrega/{actaEntregaId}/condiciones")
public class ActaEntregaCondicionController {

    private final ActaEntregaCondicionService actaEntregaCondicionService;

    public ActaEntregaCondicionController(ActaEntregaCondicionService actaEntregaCondicionService) {
        this.actaEntregaCondicionService = actaEntregaCondicionService;
    }

    @PostMapping
    public List<CondicionEntregaResponse> asignar(@PathVariable UUID actaEntregaId,
                                                     @RequestBody AsignarCondicionesEntregaRequest request) {
        return actaEntregaCondicionService.asignar(actaEntregaId, request);
    }

    @GetMapping
    public List<CondicionEntregaResponse> listar(@PathVariable UUID actaEntregaId) {
        return actaEntregaCondicionService.listarPorActa(actaEntregaId);
    }
}
