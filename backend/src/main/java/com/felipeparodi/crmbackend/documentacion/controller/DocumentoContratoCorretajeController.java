package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.DocumentoContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.service.DocumentoContratoCorretajeService;
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
@RequestMapping("/api/contratos-corretaje/{contratoCorretajeId}/documentos")
public class DocumentoContratoCorretajeController {

    private final DocumentoContratoCorretajeService documentoContratoCorretajeService;

    public DocumentoContratoCorretajeController(DocumentoContratoCorretajeService documentoContratoCorretajeService) {
        this.documentoContratoCorretajeService = documentoContratoCorretajeService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoContratoCorretajeResponse agregar(@PathVariable UUID contratoCorretajeId,
                                                        @RequestBody DocumentoContratoCorretajeRequest request) {
        return documentoContratoCorretajeService.agregar(contratoCorretajeId, request);
    }

    @GetMapping
    public List<DocumentoContratoCorretajeResponse> listar(@PathVariable UUID contratoCorretajeId) {
        return documentoContratoCorretajeService.listarPorContrato(contratoCorretajeId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoContratoCorretajeResponse subir(@PathVariable UUID contratoCorretajeId,
                                                      @RequestParam("tipo") String tipo,
                                                      @RequestParam("archivo") MultipartFile archivo) {
        return documentoContratoCorretajeService.subir(contratoCorretajeId, tipo, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID contratoCorretajeId, @PathVariable UUID id) {
        documentoContratoCorretajeService.eliminar(id);
    }
}
