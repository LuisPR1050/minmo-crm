package com.felipeparodi.crmbackend.documentacion.controller;

import com.felipeparodi.crmbackend.documentacion.dto.DocumentoOtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoOtrosiPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.service.DocumentoOtrosiPromesaService;
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
@RequestMapping("/api/otrosis-promesa/{otrosiPromesaId}/documentos")
public class DocumentoOtrosiPromesaController {

    private final DocumentoOtrosiPromesaService documentoOtrosiPromesaService;

    public DocumentoOtrosiPromesaController(DocumentoOtrosiPromesaService documentoOtrosiPromesaService) {
        this.documentoOtrosiPromesaService = documentoOtrosiPromesaService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoOtrosiPromesaResponse agregar(@PathVariable UUID otrosiPromesaId,
                                                    @RequestBody DocumentoOtrosiPromesaRequest request) {
        return documentoOtrosiPromesaService.agregar(otrosiPromesaId, request);
    }

    @GetMapping
    public List<DocumentoOtrosiPromesaResponse> listar(@PathVariable UUID otrosiPromesaId) {
        return documentoOtrosiPromesaService.listarPorOtrosi(otrosiPromesaId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public DocumentoOtrosiPromesaResponse subir(@PathVariable UUID otrosiPromesaId,
                                                  @RequestParam(value = "descripcion", required = false) String descripcion,
                                                  @RequestParam("archivo") MultipartFile archivo) {
        return documentoOtrosiPromesaService.subir(otrosiPromesaId, descripcion, archivo);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID otrosiPromesaId, @PathVariable UUID id) {
        documentoOtrosiPromesaService.eliminar(id);
    }
}
