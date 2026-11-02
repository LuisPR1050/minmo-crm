package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.DocumentoActaEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.service.DocumentoActaEntregaService;
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
@RequestMapping("/api/actas-entrega/{actaEntregaId}/documentos")
public class DocumentoActaEntregaController {

    private final DocumentoActaEntregaService documentoActaEntregaService;

    public DocumentoActaEntregaController(DocumentoActaEntregaService documentoActaEntregaService) {
        this.documentoActaEntregaService = documentoActaEntregaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoActaEntregaResponse agregar(@PathVariable UUID actaEntregaId,
                                                  @RequestBody DocumentoActaEntregaRequest request) {
        return documentoActaEntregaService.agregar(actaEntregaId, request);
    }

    @GetMapping
    public List<DocumentoActaEntregaResponse> listar(@PathVariable UUID actaEntregaId) {
        return documentoActaEntregaService.listarPorActa(actaEntregaId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoActaEntregaResponse subir(@PathVariable UUID actaEntregaId,
                                                @RequestParam("tipo") String tipo,
                                                @RequestParam("archivo") MultipartFile archivo) {
        return documentoActaEntregaService.subir(actaEntregaId, tipo, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID actaEntregaId, @PathVariable UUID id) {
        documentoActaEntregaService.eliminar(id);
    }
}
