package com.felipeparodi.crmbackend.negociacion.controller;

import com.felipeparodi.crmbackend.negociacion.dto.DocumentoNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.service.DocumentoNegociacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/documentos-negociacion")
public class DocumentoNegociacionController {

    private final DocumentoNegociacionService documentoNegociacionService;

    public DocumentoNegociacionController(DocumentoNegociacionService documentoNegociacionService) {
        this.documentoNegociacionService = documentoNegociacionService;
    }

    @PostMapping("/negociaciones/{negociacionId}")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoNegociacionResponse subir(@PathVariable UUID negociacionId,
                                               @RequestParam("tipo") String tipo,
                                               @RequestParam("archivo") MultipartFile archivo) {
        return documentoNegociacionService.subir(negociacionId, tipo, archivo);
    }

    @GetMapping
    public List<DocumentoNegociacionResponse> listar(@RequestParam UUID negociacionId) {
        return documentoNegociacionService.listarPorNegociacion(negociacionId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        documentoNegociacionService.eliminar(id);
    }
}
