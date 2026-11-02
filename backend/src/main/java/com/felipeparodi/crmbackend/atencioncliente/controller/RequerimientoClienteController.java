package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.InmuebleRecomendadoResponse;
import com.felipeparodi.crmbackend.atencioncliente.dto.RequerimientoClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.RequerimientoClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.RecomendacionInmuebleIAService;
import com.felipeparodi.crmbackend.atencioncliente.service.RequerimientoClienteService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/requerimientos-cliente")
public class RequerimientoClienteController {

    private final RequerimientoClienteService requerimientoClienteService;
    private final RecomendacionInmuebleIAService recomendacionInmuebleIAService;

    public RequerimientoClienteController(RequerimientoClienteService requerimientoClienteService,
                                           RecomendacionInmuebleIAService recomendacionInmuebleIAService) {
        this.requerimientoClienteService = requerimientoClienteService;
        this.recomendacionInmuebleIAService = recomendacionInmuebleIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequerimientoClienteResponse crear(@RequestBody RequerimientoClienteRequest request) {
        return requerimientoClienteService.crear(request);
    }

    @GetMapping
    public List<RequerimientoClienteResponse> listarPorCliente(@RequestParam UUID clienteId) {
        return requerimientoClienteService.listarPorCliente(clienteId);
    }

    @GetMapping("/{id}")
    public RequerimientoClienteResponse buscarPorId(@PathVariable UUID id) {
        return requerimientoClienteService.buscarPorId(id);
    }

    @PostMapping("/{id}/recomendaciones-ia")
    public List<InmuebleRecomendadoResponse> recomendarConIA(@PathVariable UUID id) {
        return recomendacionInmuebleIAService.recomendar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        requerimientoClienteService.eliminar(id);
    }
}