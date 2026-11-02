package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.PropietarioRequest;
import com.felipeparodi.crmbackend.captacion.dto.PropietarioResponse;
import com.felipeparodi.crmbackend.captacion.service.PropietarioService;
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
@RequestMapping("/api/propietarios")
public class PropietarioController {

    private final PropietarioService propietarioService;

    public PropietarioController(PropietarioService propietarioService) {
        this.propietarioService = propietarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public PropietarioResponse crear(@Valid @RequestBody PropietarioRequest request) {
        return propietarioService.crear(request);
    }

    @GetMapping
    public List<PropietarioResponse> listarTodos() {
        return propietarioService.listarTodos();
    }

    @GetMapping("/{id}")
    public PropietarioResponse buscarPorId(@PathVariable UUID id) {
        return propietarioService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public PropietarioResponse actualizar(@PathVariable UUID id, @Valid @RequestBody PropietarioRequest request) {
        return propietarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        propietarioService.eliminar(id);
    }
}