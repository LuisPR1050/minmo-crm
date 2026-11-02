package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasOtrosiRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.OtrosiPromesaClausulaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/otrosis-promesa/{otrosiPromesaId}/clausulas")
public class OtrosiPromesaClausulaController {

    private final OtrosiPromesaClausulaService otrosiPromesaClausulaService;

    public OtrosiPromesaClausulaController(OtrosiPromesaClausulaService otrosiPromesaClausulaService) {
        this.otrosiPromesaClausulaService = otrosiPromesaClausulaService;
    }

    @PostMapping
    public List<ClausulaCorretajeResponse> asignar(@PathVariable UUID otrosiPromesaId,
                                                     @RequestBody AsignarClausulasOtrosiRequest request) {
        return otrosiPromesaClausulaService.asignar(otrosiPromesaId, request);
    }

    @GetMapping
    public List<ClausulaCorretajeResponse> listar(@PathVariable UUID otrosiPromesaId) {
        return otrosiPromesaClausulaService.listarPorOtrosi(otrosiPromesaId);
    }
}
