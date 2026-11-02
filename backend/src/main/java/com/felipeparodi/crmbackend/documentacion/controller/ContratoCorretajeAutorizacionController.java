package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarAutorizacionesPropietarioRequest;
import com.felipeparodi.crmbackend.documentacion.dto.AutorizacionPropietarioResponse;
import com.felipeparodi.crmbackend.documentacion.service.ContratoCorretajeAutorizacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/contratos-corretaje/{contratoCorretajeId}/autorizaciones")
public class ContratoCorretajeAutorizacionController {

    private final ContratoCorretajeAutorizacionService contratoCorretajeAutorizacionService;

    public ContratoCorretajeAutorizacionController(ContratoCorretajeAutorizacionService contratoCorretajeAutorizacionService) {
        this.contratoCorretajeAutorizacionService = contratoCorretajeAutorizacionService;
    }

    @PostMapping
    public List<AutorizacionPropietarioResponse> asignar(@PathVariable UUID contratoCorretajeId,
                                                           @RequestBody AsignarAutorizacionesPropietarioRequest request) {
        return contratoCorretajeAutorizacionService.asignar(contratoCorretajeId, request);
    }

    @GetMapping
    public List<AutorizacionPropietarioResponse> listar(@PathVariable UUID contratoCorretajeId) {
        return contratoCorretajeAutorizacionService.listarPorContrato(contratoCorretajeId);
    }
}
