package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.CondicionEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.service.CondicionEntregaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/condiciones-entrega")
public class CondicionEntregaController {

    private final CondicionEntregaService condicionEntregaService;

    public CondicionEntregaController(CondicionEntregaService condicionEntregaService) {
        this.condicionEntregaService = condicionEntregaService;
    }

    @GetMapping
    public List<CondicionEntregaResponse> listar() {
        return condicionEntregaService.listarTodas();
    }
}
