package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.PazYSalvoTipoResponse;
import com.felipeparodi.crmbackend.documentacion.service.PazYSalvoTipoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/paz-y-salvo-tipos")
public class PazYSalvoTipoController {

    private final PazYSalvoTipoService pazYSalvoTipoService;

    public PazYSalvoTipoController(PazYSalvoTipoService pazYSalvoTipoService) {
        this.pazYSalvoTipoService = pazYSalvoTipoService;
    }

    @GetMapping
    public List<PazYSalvoTipoResponse> listar() {
        return pazYSalvoTipoService.listarTodas();
    }
}
