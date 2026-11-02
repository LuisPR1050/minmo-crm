package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.CondicionEspecialResponse;
import com.felipeparodi.crmbackend.negociacion.service.CondicionEspecialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/condiciones-especiales")
public class CondicionEspecialController {

    private final CondicionEspecialService condicionEspecialService;

    public CondicionEspecialController(CondicionEspecialService condicionEspecialService) {
        this.condicionEspecialService = condicionEspecialService;
    }

    @GetMapping
    public List<CondicionEspecialResponse> listarTodas() {
        return condicionEspecialService.listarTodas();
    }
}