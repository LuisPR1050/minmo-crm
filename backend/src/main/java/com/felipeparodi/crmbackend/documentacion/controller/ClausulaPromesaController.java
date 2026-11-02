package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ClausulaPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.service.ClausulaPromesaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clausulas-promesa")
public class ClausulaPromesaController {

    private final ClausulaPromesaService clausulaPromesaService;

    public ClausulaPromesaController(ClausulaPromesaService clausulaPromesaService) {
        this.clausulaPromesaService = clausulaPromesaService;
    }

    @GetMapping
    public List<ClausulaPromesaResponse> listar() {
        return clausulaPromesaService.listarTodas();
    }
}
