package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.ActualizarPublicacionInmuebleRequest;
import com.felipeparodi.crmbackend.marketing.dto.AnalisisFotosResponse;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmuebleRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmuebleResponse;
import com.felipeparodi.crmbackend.marketing.service.AnalisisFotosIAService;
import com.felipeparodi.crmbackend.marketing.service.DescripcionPublicacionIAService;
import com.felipeparodi.crmbackend.marketing.service.PublicacionInmuebleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/publicaciones-inmuebles")
public class PublicacionInmuebleController {

    private final PublicacionInmuebleService publicacionInmuebleService;
    private final DescripcionPublicacionIAService descripcionPublicacionIAService;
    private final AnalisisFotosIAService analisisFotosIAService;

    public PublicacionInmuebleController(PublicacionInmuebleService publicacionInmuebleService,
                                          DescripcionPublicacionIAService descripcionPublicacionIAService,
                                          AnalisisFotosIAService analisisFotosIAService) {
        this.publicacionInmuebleService = publicacionInmuebleService;
        this.descripcionPublicacionIAService = descripcionPublicacionIAService;
        this.analisisFotosIAService = analisisFotosIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PublicacionInmuebleResponse crear(@RequestBody PublicacionInmuebleRequest request) {
        return publicacionInmuebleService.crear(request);
    }

    @GetMapping
    public List<PublicacionInmuebleResponse> listar(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return publicacionInmuebleService.listarPorInmueble(inmuebleId);
        }
        return publicacionInmuebleService.listarTodas();
    }

    @GetMapping("/{id}")
    public PublicacionInmuebleResponse buscarPorId(@PathVariable UUID id) {
        return publicacionInmuebleService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public PublicacionInmuebleResponse actualizar(@PathVariable UUID id,
                                                    @RequestBody ActualizarPublicacionInmuebleRequest request) {
        return publicacionInmuebleService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public PublicacionInmuebleResponse cambiarEstado(@PathVariable UUID id,
                                                       @RequestBody CambiarEstadoPublicacionRequest request) {
        return publicacionInmuebleService.cambiarEstado(id, request);
    }

    @PostMapping("/{id}/generar-descripciones")
    public PublicacionInmuebleResponse generarDescripciones(@PathVariable UUID id) {
        return descripcionPublicacionIAService.generarDescripciones(id);
    }

    @PostMapping("/{id}/analisis-fotos-ia")
    public AnalisisFotosResponse analizarFotosConIA(@PathVariable UUID id) {
        return analisisFotosIAService.analizar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        publicacionInmuebleService.eliminar(id);
    }
}
