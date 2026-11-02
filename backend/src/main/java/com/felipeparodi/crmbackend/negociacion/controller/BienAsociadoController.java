package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.BienAsociadoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.BienAsociadoResponse;
import com.felipeparodi.crmbackend.negociacion.service.BienAsociadoService;
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
@RequestMapping("/api/bienes-asociados")
public class BienAsociadoController {

    private final BienAsociadoService bienAsociadoService;

    public BienAsociadoController(BienAsociadoService bienAsociadoService) {
        this.bienAsociadoService = bienAsociadoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public BienAsociadoResponse crear(@RequestBody BienAsociadoRequest request) {
        return bienAsociadoService.crear(request);
    }

    @GetMapping
    public List<BienAsociadoResponse> listarPorNegociacion(@RequestParam UUID negociacionId) {
        return bienAsociadoService.listarPorNegociacion(negociacionId);
    }

    @PatchMapping("/{id}")
    public BienAsociadoResponse actualizar(@PathVariable UUID id, @RequestBody BienAsociadoRequest request) {
        return bienAsociadoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        bienAsociadoService.eliminar(id);
    }
}