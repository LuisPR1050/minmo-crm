package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.service.ParteNegociacionService;
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
@RequestMapping("/api/partes-negociacion")
public class ParteNegociacionController {

    private final ParteNegociacionService parteNegociacionService;

    public ParteNegociacionController(ParteNegociacionService parteNegociacionService) {
        this.parteNegociacionService = parteNegociacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ParteNegociacionResponse crear(@RequestBody ParteNegociacionRequest request) {
        return parteNegociacionService.crear(request);
    }

    @GetMapping
    public List<ParteNegociacionResponse> listarPorNegociacion(@RequestParam UUID negociacionId) {
        return parteNegociacionService.listarPorNegociacion(negociacionId);
    }

    @PatchMapping("/{id}")
    public ParteNegociacionResponse actualizar(@PathVariable UUID id, @RequestBody ParteNegociacionRequest request) {
        return parteNegociacionService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        parteNegociacionService.eliminar(id);
    }
}