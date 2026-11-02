package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ActualizarContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CambiarEstadoContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.dto.GenerarTextoIARequest;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenContratosCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.ContratoCorretajeService;
import com.felipeparodi.crmbackend.documentacion.service.RedaccionContratoCorretajeIAService;
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
@RequestMapping("/api/contratos-corretaje")
public class ContratoCorretajeController {

    private final ContratoCorretajeService contratoCorretajeService;
    private final RedaccionContratoCorretajeIAService redaccionContratoCorretajeIAService;

    public ContratoCorretajeController(ContratoCorretajeService contratoCorretajeService,
                                        RedaccionContratoCorretajeIAService redaccionContratoCorretajeIAService) {
        this.contratoCorretajeService = contratoCorretajeService;
        this.redaccionContratoCorretajeIAService = redaccionContratoCorretajeIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ContratoCorretajeResponse crear(@jakarta.validation.Valid @RequestBody ContratoCorretajeRequest request) {
        return contratoCorretajeService.crear(request);
    }

    @GetMapping
    public List<ContratoCorretajeResponse> listarTodos(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return contratoCorretajeService.listarPorInmueble(inmuebleId);
        }
        return contratoCorretajeService.listarTodos();
    }

    @GetMapping("/resumen")
    public ResumenContratosCorretajeResponse resumenPorEstado() {
        return contratoCorretajeService.resumenPorEstado();
    }

    @GetMapping("/{id}")
    public ContratoCorretajeResponse buscarPorId(@PathVariable UUID id) {
        return contratoCorretajeService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public ContratoCorretajeResponse actualizar(@PathVariable UUID id,
                                                 @RequestBody ActualizarContratoCorretajeRequest request) {
        return contratoCorretajeService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public ContratoCorretajeResponse cambiarEstado(@PathVariable UUID id,
                                                    @RequestBody CambiarEstadoContratoCorretajeRequest request) {
        return contratoCorretajeService.cambiarEstado(id, request);
    }

    @PostMapping("/{id}/generar-texto")
    public ContratoCorretajeResponse generarTexto(@PathVariable UUID id,
                                                    @RequestBody(required = false) GenerarTextoIARequest request) {
        String instrucciones = request != null ? request.getInstruccionesAdicionales() : null;
        return redaccionContratoCorretajeIAService.generarTexto(id, instrucciones);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        contratoCorretajeService.eliminar(id);
    }
}
