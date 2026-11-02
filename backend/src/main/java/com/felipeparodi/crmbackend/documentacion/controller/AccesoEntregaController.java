package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AccesoEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.service.AccesoEntregaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/accesos-entrega")
public class AccesoEntregaController {

    private final AccesoEntregaService accesoEntregaService;

    public AccesoEntregaController(AccesoEntregaService accesoEntregaService) {
        this.accesoEntregaService = accesoEntregaService;
    }

    @GetMapping
    public List<AccesoEntregaResponse> listar() {
        return accesoEntregaService.listarTodas();
    }
}
