package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.PortalInmobiliarioResponse;
import com.felipeparodi.crmbackend.marketing.service.PortalInmobiliarioService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/portales-inmobiliarios")
public class PortalInmobiliarioController {

    private final PortalInmobiliarioService portalInmobiliarioService;

    public PortalInmobiliarioController(PortalInmobiliarioService portalInmobiliarioService) {
        this.portalInmobiliarioService = portalInmobiliarioService;
    }

    @GetMapping
    public List<PortalInmobiliarioResponse> listar() {
        return portalInmobiliarioService.listarTodos();
    }
}
