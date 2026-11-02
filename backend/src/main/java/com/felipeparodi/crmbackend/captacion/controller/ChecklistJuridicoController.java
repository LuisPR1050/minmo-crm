package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.ChecklistJuridicoRequest;
import com.felipeparodi.crmbackend.captacion.dto.ChecklistJuridicoResponse;
import com.felipeparodi.crmbackend.captacion.service.ChecklistJuridicoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/checklists-juridicos")
public class ChecklistJuridicoController {

    private final ChecklistJuridicoService checklistJuridicoService;

    public ChecklistJuridicoController(ChecklistJuridicoService checklistJuridicoService) {
        this.checklistJuridicoService = checklistJuridicoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ChecklistJuridicoResponse crear(@RequestBody ChecklistJuridicoRequest request) {
        return checklistJuridicoService.crear(request);
    }

    @GetMapping
    public List<ChecklistJuridicoResponse> listarTodos() {
        return checklistJuridicoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ChecklistJuridicoResponse buscarPorId(@PathVariable UUID id) {
        return checklistJuridicoService.buscarPorId(id);
    }

    @GetMapping("/por-inmueble/{inmuebleId}")
    public ChecklistJuridicoResponse buscarPorInmueble(@PathVariable UUID inmuebleId) {
        return checklistJuridicoService.buscarPorInmueble(inmuebleId);
    }
}