package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.DocumentoPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoPromesaCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.service.DocumentoPromesaCompraventaService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/promesas-compraventa/{promesaCompraventaId}/documentos")
public class DocumentoPromesaCompraventaController {

    private final DocumentoPromesaCompraventaService documentoPromesaCompraventaService;

    public DocumentoPromesaCompraventaController(DocumentoPromesaCompraventaService documentoPromesaCompraventaService) {
        this.documentoPromesaCompraventaService = documentoPromesaCompraventaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoPromesaCompraventaResponse agregar(@PathVariable UUID promesaCompraventaId,
                                                         @RequestBody DocumentoPromesaCompraventaRequest request) {
        return documentoPromesaCompraventaService.agregar(promesaCompraventaId, request);
    }

    @GetMapping
    public List<DocumentoPromesaCompraventaResponse> listar(@PathVariable UUID promesaCompraventaId) {
        return documentoPromesaCompraventaService.listarPorPromesa(promesaCompraventaId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoPromesaCompraventaResponse subir(@PathVariable UUID promesaCompraventaId,
                                                        @RequestParam("tipo") String tipo,
                                                        @RequestParam("archivo") MultipartFile archivo) {
        return documentoPromesaCompraventaService.subir(promesaCompraventaId, tipo, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID promesaCompraventaId, @PathVariable UUID id) {
        documentoPromesaCompraventaService.eliminar(id);
    }
}
