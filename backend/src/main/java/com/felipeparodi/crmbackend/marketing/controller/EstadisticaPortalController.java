package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.EstadisticaPortalRequest;
import com.felipeparodi.crmbackend.marketing.dto.EstadisticaPortalResponse;
import com.felipeparodi.crmbackend.marketing.dto.ResumenEstadisticasPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.service.EstadisticaPortalService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class EstadisticaPortalController {

    private final EstadisticaPortalService estadisticaPortalService;

    public EstadisticaPortalController(EstadisticaPortalService estadisticaPortalService) {
        this.estadisticaPortalService = estadisticaPortalService;
    }

    @PostMapping("/api/publicacion-portal/{vinculoId}/estadisticas")
    @ResponseStatus(HttpStatus.CREATED)
    public EstadisticaPortalResponse registrar(@PathVariable UUID vinculoId,
                                                 @RequestBody EstadisticaPortalRequest request) {
        return estadisticaPortalService.registrar(vinculoId, request);
    }

    @GetMapping("/api/publicacion-portal/{vinculoId}/estadisticas")
    public List<EstadisticaPortalResponse> listar(@PathVariable UUID vinculoId) {
        return estadisticaPortalService.listarPorVinculo(vinculoId);
    }

    @GetMapping("/api/publicaciones-inmuebles/{publicacionInmuebleId}/estadisticas/resumen")
    public ResumenEstadisticasPublicacionResponse resumen(@PathVariable UUID publicacionInmuebleId) {
        return estadisticaPortalService.resumenPorPublicacion(publicacionInmuebleId);
    }
}
