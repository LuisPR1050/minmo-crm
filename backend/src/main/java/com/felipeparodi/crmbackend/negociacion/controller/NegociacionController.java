package com.felipeparodi.crmbackend.negociacion.controller;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.ActualizarCondicionesRequest;
import com.felipeparodi.crmbackend.negociacion.dto.CambiarEstadoNegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.service.NegociacionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/negociaciones")
public class NegociacionController {

    private final NegociacionService negociacionService;

    public NegociacionController(NegociacionService negociacionService) {
        this.negociacionService = negociacionService;
    }
@PatchMapping("/{id}/escritura-firmada")
    public NegociacionResponse marcarEscrituraFirmada(@PathVariable UUID id,
                                                       @RequestBody MarcarHitoRequest request) {
        return negociacionService.marcarEscrituraFirmada(id, request);
    }

    @PatchMapping("/{id}/pago-recibido")
    public NegociacionResponse marcarPagoRecibido(@PathVariable UUID id,
                                                   @RequestBody MarcarHitoRequest request) {
        return negociacionService.marcarPagoRecibido(id, request);
    }

    @PatchMapping("/{id}/entrega-realizada")
    public NegociacionResponse marcarEntregaRealizada(@PathVariable UUID id,
                                                        @RequestBody MarcarHitoRequest request) {
        return negociacionService.marcarEntregaRealizada(id, request);
    }
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public NegociacionResponse crear(@Valid @RequestBody NegociacionRequest request) {
        return negociacionService.crear(request);
    }

    @GetMapping
    public List<NegociacionResponse> listarTodos() {
        return negociacionService.listarTodos();
    }

    @GetMapping("/{id}")
    public NegociacionResponse buscarPorId(@PathVariable UUID id) {
        return negociacionService.buscarPorId(id);
    }

    @PatchMapping("/{id}/condiciones")
    public NegociacionResponse actualizarCondiciones(@PathVariable UUID id,
                                                       @RequestBody ActualizarCondicionesRequest request) {
        return negociacionService.actualizarCondiciones(id, request);
    }

    @PatchMapping("/{id}/estado")
    public NegociacionResponse cambiarEstado(@PathVariable UUID id,
                                              @RequestBody CambiarEstadoNegociacionRequest request) {
        return negociacionService.cambiarEstado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        negociacionService.eliminar(id);
    }
}