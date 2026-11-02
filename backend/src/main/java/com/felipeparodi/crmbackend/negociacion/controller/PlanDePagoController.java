package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.PlanDePagoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.PlanDePagoResponse;
import com.felipeparodi.crmbackend.negociacion.service.PlanDePagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/planes-pago")
public class PlanDePagoController {

    private final PlanDePagoService planDePagoService;

    public PlanDePagoController(PlanDePagoService planDePagoService) {
        this.planDePagoService = planDePagoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PlanDePagoResponse crear(@RequestBody PlanDePagoRequest request) {
        return planDePagoService.crear(request);
    }

    @GetMapping
    public List<PlanDePagoResponse> listarPorNegociacion(@RequestParam UUID negociacionId) {
        return planDePagoService.listarPorNegociacion(negociacionId);
    }

    @PatchMapping("/{id}/pagada")
    public PlanDePagoResponse marcarPagada(@PathVariable UUID id, @RequestBody MarcarHitoRequest request) {
        return planDePagoService.marcarPagada(id, request.isValor());
    }

    @PatchMapping("/{id}")
    public PlanDePagoResponse actualizar(@PathVariable UUID id, @RequestBody PlanDePagoRequest request) {
        return planDePagoService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        planDePagoService.eliminar(id);
    }
}