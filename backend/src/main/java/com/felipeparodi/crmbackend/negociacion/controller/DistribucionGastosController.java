package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.DistribucionGastosRequest;
import com.felipeparodi.crmbackend.negociacion.dto.DistribucionGastosResponse;
import com.felipeparodi.crmbackend.negociacion.service.DistribucionGastosService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/distribucion-gastos")
public class DistribucionGastosController {

    private final DistribucionGastosService distribucionGastosService;

    public DistribucionGastosController(DistribucionGastosService distribucionGastosService) {
        this.distribucionGastosService = distribucionGastosService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DistribucionGastosResponse crear(@RequestBody DistribucionGastosRequest request) {
        return distribucionGastosService.crear(request);
    }

    @GetMapping("/por-negociacion/{negociacionId}")
    public DistribucionGastosResponse buscarPorNegociacion(@PathVariable UUID negociacionId) {
        return distribucionGastosService.buscarPorNegociacion(negociacionId);
    }
}