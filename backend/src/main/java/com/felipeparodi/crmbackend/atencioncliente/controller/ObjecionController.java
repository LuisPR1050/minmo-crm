package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.ObjecionResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.ObjecionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/objeciones")
public class ObjecionController {

    private final ObjecionService objecionService;

    public ObjecionController(ObjecionService objecionService) {
        this.objecionService = objecionService;
    }

    @GetMapping
    public List<ObjecionResponse> listarTodas() {
        return objecionService.listarTodas();
    }
}