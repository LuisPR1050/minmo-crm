package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.DocumentoReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.service.DocumentoReciboPagoService;
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
@RequestMapping("/api/recibos-pago/{reciboPagoId}/documentos")
public class DocumentoReciboPagoController {

    private final DocumentoReciboPagoService documentoReciboPagoService;

    public DocumentoReciboPagoController(DocumentoReciboPagoService documentoReciboPagoService) {
        this.documentoReciboPagoService = documentoReciboPagoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoReciboPagoResponse agregar(@PathVariable UUID reciboPagoId,
                                                 @RequestBody DocumentoReciboPagoRequest request) {
        return documentoReciboPagoService.agregar(reciboPagoId, request);
    }

    @GetMapping
    public List<DocumentoReciboPagoResponse> listar(@PathVariable UUID reciboPagoId) {
        return documentoReciboPagoService.listarPorRecibo(reciboPagoId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoReciboPagoResponse subir(@PathVariable UUID reciboPagoId,
                                               @RequestParam("archivo") MultipartFile archivo) {
        return documentoReciboPagoService.subir(reciboPagoId, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID reciboPagoId, @PathVariable UUID id) {
        documentoReciboPagoService.eliminar(id);
    }
}
