package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.CambiarEstadoVisitaRequest;
import com.felipeparodi.crmbackend.captacion.dto.VisitaCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.VisitaCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.service.VisitaCaptacionService;
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
@RequestMapping("/api/visitas-captacion")
public class VisitaCaptacionController {

    private final VisitaCaptacionService visitaCaptacionService;

    public VisitaCaptacionController(VisitaCaptacionService visitaCaptacionService) {
        this.visitaCaptacionService = visitaCaptacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitaCaptacionResponse crear(@RequestBody VisitaCaptacionRequest request) {
        return visitaCaptacionService.crear(request);
    }

    @GetMapping
    public List<VisitaCaptacionResponse> listarTodos(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return visitaCaptacionService.listarPorInmueble(inmuebleId);
        }
        return visitaCaptacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public VisitaCaptacionResponse buscarPorId(@PathVariable UUID id) {
        return visitaCaptacionService.buscarPorId(id);
    }

    @PatchMapping("/{id}/estado")
    public VisitaCaptacionResponse cambiarEstado(@PathVariable UUID id,
@RequestBody CambiarEstadoVisitaRequest request) {
        return visitaCaptacionService.cambiarEstado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        visitaCaptacionService.eliminar(id);
    }
}