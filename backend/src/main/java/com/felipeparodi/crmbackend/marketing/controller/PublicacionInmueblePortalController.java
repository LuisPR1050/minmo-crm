package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.AsignarPortalesRequest;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoPublicacionPortalRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmueblePortalResponse;
import com.felipeparodi.crmbackend.marketing.service.PublicacionInmueblePortalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
public class PublicacionInmueblePortalController {

    private final PublicacionInmueblePortalService publicacionInmueblePortalService;

    public PublicacionInmueblePortalController(PublicacionInmueblePortalService publicacionInmueblePortalService) {
        this.publicacionInmueblePortalService = publicacionInmueblePortalService;
    }

    @PostMapping("/api/publicaciones-inmuebles/{publicacionInmuebleId}/portales")
    public List<PublicacionInmueblePortalResponse> asignar(@PathVariable UUID publicacionInmuebleId,
                                                             @RequestBody AsignarPortalesRequest request) {
        return publicacionInmueblePortalService.asignar(publicacionInmuebleId, request);
    }

    @GetMapping("/api/publicaciones-inmuebles/{publicacionInmuebleId}/portales")
    public List<PublicacionInmueblePortalResponse> listar(@PathVariable UUID publicacionInmuebleId) {
        return publicacionInmueblePortalService.listarPorPublicacion(publicacionInmuebleId);
    }

    @PatchMapping("/api/publicacion-portal/{vinculoId}/estado")
    public PublicacionInmueblePortalResponse cambiarEstado(@PathVariable UUID vinculoId,
                                                             @RequestBody CambiarEstadoPublicacionPortalRequest request) {
        return publicacionInmueblePortalService.cambiarEstado(vinculoId, request);
    }
}
