package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.CambiarEstadoVisitaClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ConfirmarVisitaRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.VisitaClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.VisitaClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.VisitaClienteService;
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
@RequestMapping("/api/visitas-cliente")
public class VisitaClienteController {

    private final VisitaClienteService visitaClienteService;

    public VisitaClienteController(VisitaClienteService visitaClienteService) {
        this.visitaClienteService = visitaClienteService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VisitaClienteResponse crear(@RequestBody VisitaClienteRequest request) {
        return visitaClienteService.crear(request);
    }

    @GetMapping
    public List<VisitaClienteResponse> listarPorCliente(@RequestParam(required = false) UUID clienteId) {
        if (clienteId != null) {
            return visitaClienteService.listarPorCliente(clienteId);
        }
        return visitaClienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public VisitaClienteResponse buscarPorId(@PathVariable UUID id) {
        return visitaClienteService.buscarPorId(id);
    }

    @PatchMapping("/{id}/confirmar")
    public VisitaClienteResponse confirmar(@PathVariable UUID id,
                                            @RequestBody ConfirmarVisitaRequest request) {
        return visitaClienteService.confirmar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public VisitaClienteResponse cambiarEstado(@PathVariable UUID id,
                                                @RequestBody CambiarEstadoVisitaClienteRequest request) {
        return visitaClienteService.cambiarEstado(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        visitaClienteService.eliminar(id);
    }
}