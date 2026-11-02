package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.ComparacionDocumentosResponse;
import com.felipeparodi.crmbackend.documentacion.service.ComparacionDocumentosIAService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/documentos")
public class ComparacionDocumentosController {

    private final ComparacionDocumentosIAService comparacionDocumentosIAService;

    public ComparacionDocumentosController(ComparacionDocumentosIAService comparacionDocumentosIAService) {
        this.comparacionDocumentosIAService = comparacionDocumentosIAService;
    }

    @PostMapping("/comparar-ia")
    public ComparacionDocumentosResponse compararConIA(@RequestParam("documentoA") MultipartFile documentoA,
                                                         @RequestParam("documentoB") MultipartFile documentoB) {
        return comparacionDocumentosIAService.comparar(documentoA, documentoB);
    }
}
