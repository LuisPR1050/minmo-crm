package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AuditoriaFinancieraResponse;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenPagosNegociacionResponse;
import com.felipeparodi.crmbackend.documentacion.service.AuditoriaFinancieraIAService;
import com.felipeparodi.crmbackend.documentacion.service.ReciboPagoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/recibos-pago")
public class ReciboPagoController {

    private final ReciboPagoService reciboPagoService;
    private final AuditoriaFinancieraIAService auditoriaFinancieraIAService;

    public ReciboPagoController(ReciboPagoService reciboPagoService,
                                 AuditoriaFinancieraIAService auditoriaFinancieraIAService) {
        this.reciboPagoService = reciboPagoService;
        this.auditoriaFinancieraIAService = auditoriaFinancieraIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ReciboPagoResponse crear(@RequestBody ReciboPagoRequest request) {
        return reciboPagoService.crear(request);
    }

    @GetMapping
    public List<ReciboPagoResponse> listarPorNegociacion(@RequestParam UUID negociacionId) {
        return reciboPagoService.listarPorNegociacion(negociacionId);
    }

    @GetMapping("/{id}")
    public ReciboPagoResponse buscarPorId(@PathVariable UUID id) {
        return reciboPagoService.buscarPorId(id);
    }

    @GetMapping("/resumen")
    public ResumenPagosNegociacionResponse resumenPagos(@RequestParam UUID negociacionId) {
        return reciboPagoService.resumenPagos(negociacionId);
    }

    @PostMapping("/auditoria-financiera")
    public AuditoriaFinancieraResponse auditoriaFinanciera(@RequestParam UUID negociacionId) {
        return auditoriaFinancieraIAService.auditar(negociacionId);
    }
}
