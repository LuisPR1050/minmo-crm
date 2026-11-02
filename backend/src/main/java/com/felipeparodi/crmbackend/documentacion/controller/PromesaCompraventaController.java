package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ActualizarPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CambiarEstadoPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PromesaCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.dto.GenerarTextoIARequest;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenPromesasCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.service.PromesaCompraventaService;
import com.felipeparodi.crmbackend.documentacion.service.RedaccionPromesaCompraventaIAService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promesas-compraventa")
public class PromesaCompraventaController {

    private final PromesaCompraventaService promesaCompraventaService;
    private final RedaccionPromesaCompraventaIAService redaccionPromesaCompraventaIAService;

    public PromesaCompraventaController(PromesaCompraventaService promesaCompraventaService,
                                         RedaccionPromesaCompraventaIAService redaccionPromesaCompraventaIAService) {
        this.promesaCompraventaService = promesaCompraventaService;
        this.redaccionPromesaCompraventaIAService = redaccionPromesaCompraventaIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PromesaCompraventaResponse crear(@jakarta.validation.Valid @RequestBody PromesaCompraventaRequest request) {
        return promesaCompraventaService.crear(request);
    }

    @GetMapping
    public List<PromesaCompraventaResponse> listarTodos() {
        return promesaCompraventaService.listarTodos();
    }

    @GetMapping("/resumen")
    public ResumenPromesasCompraventaResponse resumenPorEstado() {
        return promesaCompraventaService.resumenPorEstado();
    }

    @GetMapping("/{id}")
    public PromesaCompraventaResponse buscarPorId(@PathVariable UUID id) {
        return promesaCompraventaService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public PromesaCompraventaResponse actualizar(@PathVariable UUID id,
                                                  @RequestBody ActualizarPromesaCompraventaRequest request) {
        return promesaCompraventaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public PromesaCompraventaResponse cambiarEstado(@PathVariable UUID id,
                                                     @RequestBody CambiarEstadoPromesaCompraventaRequest request) {
        return promesaCompraventaService.cambiarEstado(id, request);
    }

    @PostMapping("/{id}/generar-texto")
    public PromesaCompraventaResponse generarTexto(@PathVariable UUID id,
                                                     @RequestBody(required = false) GenerarTextoIARequest request) {
        String instrucciones = request != null ? request.getInstruccionesAdicionales() : null;
        return redaccionPromesaCompraventaIAService.generarTexto(id, instrucciones);
    }
}
