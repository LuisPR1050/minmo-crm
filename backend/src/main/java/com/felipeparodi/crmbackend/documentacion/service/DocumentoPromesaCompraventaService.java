package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoPromesaCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.DocumentoPromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.entity.TipoDocumentoPromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoPromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoPromesaCompraventaService {

    private static final String SUBCARPETA_PROMESAS_COMPRAVENTA = "promesas-compraventa";

    private final DocumentoPromesaCompraventaRepository documentoPromesaCompraventaRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoPromesaCompraventaService(DocumentoPromesaCompraventaRepository documentoPromesaCompraventaRepository,
                                               PromesaCompraventaRepository promesaCompraventaRepository,
                                               AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoPromesaCompraventaRepository = documentoPromesaCompraventaRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoPromesaCompraventaResponse agregar(UUID promesaCompraventaId, DocumentoPromesaCompraventaRequest request) {
        PromesaCompraventa promesa = promesaCompraventaRepository.findById(promesaCompraventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Promesa de compraventa no encontrada con id: " + promesaCompraventaId));

        DocumentoPromesaCompraventa documento = DocumentoPromesaCompraventa.builder()
                .promesaCompraventa(promesa)
                .tipo(TipoDocumentoPromesaCompraventa.valueOf(request.getTipo()))
                .url(request.getUrl())
                .build();

        DocumentoPromesaCompraventa guardado = documentoPromesaCompraventaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public DocumentoPromesaCompraventaResponse subir(UUID promesaCompraventaId, String tipo, MultipartFile archivo) {
        PromesaCompraventa promesa = promesaCompraventaRepository.findById(promesaCompraventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Promesa de compraventa no encontrada con id: " + promesaCompraventaId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_PROMESAS_COMPRAVENTA, archivo);

        DocumentoPromesaCompraventa documento = DocumentoPromesaCompraventa.builder()
                .promesaCompraventa(promesa)
                .tipo(TipoDocumentoPromesaCompraventa.valueOf(tipo))
                .url(rutaPublica)
                .build();

        DocumentoPromesaCompraventa guardado = documentoPromesaCompraventaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoPromesaCompraventaResponse> listarPorPromesa(UUID promesaCompraventaId) {
        return documentoPromesaCompraventaRepository.findByPromesaCompraventaId(promesaCompraventaId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoPromesaCompraventa documento = documentoPromesaCompraventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoPromesaCompraventaRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoPromesaCompraventaResponse mapearAResponse(DocumentoPromesaCompraventa documento) {
        return DocumentoPromesaCompraventaResponse.builder()
                .id(documento.getId())
                .tipo(documento.getTipo().name())
                .url(documento.getUrl())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
