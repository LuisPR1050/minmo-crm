package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.AsignarCaracteristicasRequest;
import com.felipeparodi.crmbackend.captacion.dto.CaracteristicaResponse;
import com.felipeparodi.crmbackend.captacion.service.InmuebleCaracteristicaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inmuebles/{inmuebleId}/caracteristicas")
public class InmuebleCaracteristicaController {

    private final InmuebleCaracteristicaService inmuebleCaracteristicaService;

    public InmuebleCaracteristicaController(InmuebleCaracteristicaService inmuebleCaracteristicaService) {
        this.inmuebleCaracteristicaService = inmuebleCaracteristicaService;
    }

    @PostMapping
    public List<CaracteristicaResponse> asignar(@PathVariable UUID inmuebleId,
        @RequestBody AsignarCaracteristicasRequest request) {
        return inmuebleCaracteristicaService.asignar(inmuebleId, request);
    }

    @GetMapping
    public List<CaracteristicaResponse> listar(@PathVariable UUID inmuebleId) {
        return inmuebleCaracteristicaService.listarPorInmueble(inmuebleId);
    }
}