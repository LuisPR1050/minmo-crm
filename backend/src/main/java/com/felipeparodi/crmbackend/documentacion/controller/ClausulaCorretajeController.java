package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.ClausulaCorretajeService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/clausulas-corretaje")
public class ClausulaCorretajeController {

    private final ClausulaCorretajeService clausulaCorretajeService;

    public ClausulaCorretajeController(ClausulaCorretajeService clausulaCorretajeService) {
        this.clausulaCorretajeService = clausulaCorretajeService;
    }

    @GetMapping
    public List<ClausulaCorretajeResponse> listar() {
        return clausulaCorretajeService.listarTodas();
    }
}
