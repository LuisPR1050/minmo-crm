package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.AsignarCondicionesEspecialesRequest;
import com.felipeparodi.crmbackend.negociacion.dto.CondicionEspecialResponse;
import com.felipeparodi.crmbackend.negociacion.service.NegociacionCondicionEspecialService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/negociaciones/{negociacionId}/condiciones-especiales")
public class NegociacionCondicionEspecialController {

    private final NegociacionCondicionEspecialService negociacionCondicionEspecialService;

    public NegociacionCondicionEspecialController(NegociacionCondicionEspecialService negociacionCondicionEspecialService) {
        this.negociacionCondicionEspecialService = negociacionCondicionEspecialService;
    }

    @PostMapping
    public List<CondicionEspecialResponse> asignar(@PathVariable UUID negociacionId,
                                                     @RequestBody AsignarCondicionesEspecialesRequest request) {
        return negociacionCondicionEspecialService.asignar(negociacionId, request);
    }

    @GetMapping
    public List<CondicionEspecialResponse> listar(@PathVariable UUID negociacionId) {
        return negociacionCondicionEspecialService.listarPorNegociacion(negociacionId);
    }
}