package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesResponse;
import com.felipeparodi.crmbackend.atencioncliente.dto.CambiarEstadoPipelineRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.AnalisisObjecionesIAService;
import com.felipeparodi.crmbackend.atencioncliente.service.ClienteService;
import jakarta.validation.Valid;
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
@RequestMapping("/api/clientes")
public class ClienteController {

    private final ClienteService clienteService;
    private final AnalisisObjecionesIAService analisisObjecionesIAService;

    public ClienteController(ClienteService clienteService, AnalisisObjecionesIAService analisisObjecionesIAService) {
        this.clienteService = clienteService;
        this.analisisObjecionesIAService = analisisObjecionesIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse crear(@Valid @RequestBody ClienteRequest request) {
        return clienteService.crear(request);
    }

    @GetMapping
    public List<ClienteResponse> listarTodos() {
        return clienteService.listarTodos();
    }

    @GetMapping("/{id}")
    public ClienteResponse buscarPorId(@PathVariable UUID id) {
        return clienteService.buscarPorId(id);
    }

    @PatchMapping("/{id}/estado-pipeline")
    public ClienteResponse cambiarEstadoPipeline(@PathVariable UUID id,
@RequestBody CambiarEstadoPipelineRequest request) {
        return clienteService.cambiarEstadoPipeline(id, request);
    }

    @PostMapping("/{id}/analisis-objeciones-ia")
    public AnalisisObjecionesResponse analizarObjecionesConIA(@PathVariable UUID id) {
        return analisisObjecionesIAService.analizar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean confirmar) {
        clienteService.eliminar(id, confirmar);
    }
}