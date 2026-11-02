package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarPazYSalvosRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PazYSalvoTipoResponse;
import com.felipeparodi.crmbackend.documentacion.service.ActaEntregaPazYSalvoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/actas-entrega/{actaEntregaId}/paz-y-salvos")
public class ActaEntregaPazYSalvoController {

    private final ActaEntregaPazYSalvoService actaEntregaPazYSalvoService;

    public ActaEntregaPazYSalvoController(ActaEntregaPazYSalvoService actaEntregaPazYSalvoService) {
        this.actaEntregaPazYSalvoService = actaEntregaPazYSalvoService;
    }

    @PostMapping
    public List<PazYSalvoTipoResponse> asignar(@PathVariable UUID actaEntregaId,
                                                 @RequestBody AsignarPazYSalvosRequest request) {
        return actaEntregaPazYSalvoService.asignar(actaEntregaId, request);
    }

    @GetMapping
    public List<PazYSalvoTipoResponse> listar(@PathVariable UUID actaEntregaId) {
        return actaEntregaPazYSalvoService.listarPorActa(actaEntregaId);
    }
}
