package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.ProyeccionGastosResponse;
import com.felipeparodi.crmbackend.negociacion.service.ProyeccionGastosService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/negociaciones/{negociacionId}/proyeccion-gastos")
public class ProyeccionGastosController {

    private final ProyeccionGastosService proyeccionGastosService;

    public ProyeccionGastosController(ProyeccionGastosService proyeccionGastosService) {
        this.proyeccionGastosService = proyeccionGastosService;
    }

    @GetMapping
    public ProyeccionGastosResponse calcular(@PathVariable UUID negociacionId) {
        return proyeccionGastosService.calcular(negociacionId);
    }
}