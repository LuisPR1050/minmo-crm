package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ActualizarOtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.GenerarTextoIARequest;
import com.felipeparodi.crmbackend.documentacion.dto.OtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.OtrosiPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.service.OtrosiPromesaService;
import com.felipeparodi.crmbackend.documentacion.service.RedaccionOtrosiPromesaIAService;
import org.springframework.http.HttpStatus;
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
@RequestMapping("/api/otrosis-promesa")
public class OtrosiPromesaController {

    private final OtrosiPromesaService otrosiPromesaService;
    private final RedaccionOtrosiPromesaIAService redaccionOtrosiPromesaIAService;

    public OtrosiPromesaController(OtrosiPromesaService otrosiPromesaService,
                                    RedaccionOtrosiPromesaIAService redaccionOtrosiPromesaIAService) {
        this.otrosiPromesaService = otrosiPromesaService;
        this.redaccionOtrosiPromesaIAService = redaccionOtrosiPromesaIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public OtrosiPromesaResponse crear(@jakarta.validation.Valid @RequestBody OtrosiPromesaRequest request) {
        return otrosiPromesaService.crear(request);
    }

    @GetMapping
    public List<OtrosiPromesaResponse> listar(@RequestParam(required = false) UUID promesaCompraventaId) {
        if (promesaCompraventaId != null) {
            return otrosiPromesaService.listarPorPromesa(promesaCompraventaId);
        }
        return otrosiPromesaService.listarTodos();
    }

    @GetMapping("/{id}")
    public OtrosiPromesaResponse buscarPorId(@PathVariable UUID id) {
        return otrosiPromesaService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public OtrosiPromesaResponse actualizar(@PathVariable UUID id,
                                             @RequestBody ActualizarOtrosiPromesaRequest request) {
        return otrosiPromesaService.actualizar(id, request);
    }

    @PatchMapping("/{id}/aplicar")
    public OtrosiPromesaResponse aplicar(@PathVariable UUID id) {
        return otrosiPromesaService.aplicar(id);
    }

    @PostMapping("/{id}/generar-texto")
    public OtrosiPromesaResponse generarTexto(@PathVariable UUID id,
                                                @RequestBody(required = false) GenerarTextoIARequest request) {
        String instrucciones = request != null ? request.getInstruccionesAdicionales() : null;
        return redaccionOtrosiPromesaIAService.generarTexto(id, instrucciones);
    }
}
