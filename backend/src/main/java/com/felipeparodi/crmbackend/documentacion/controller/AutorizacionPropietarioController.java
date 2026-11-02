package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AutorizacionPropietarioResponse;
import com.felipeparodi.crmbackend.documentacion.service.AutorizacionPropietarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/autorizaciones-propietario")
public class AutorizacionPropietarioController {

    private final AutorizacionPropietarioService autorizacionPropietarioService;

    public AutorizacionPropietarioController(AutorizacionPropietarioService autorizacionPropietarioService) {
        this.autorizacionPropietarioService = autorizacionPropietarioService;
    }

    @GetMapping
    public List<AutorizacionPropietarioResponse> listar() {
        return autorizacionPropietarioService.listarTodas();
    }
}
