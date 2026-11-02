package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoActaEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.DocumentoActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.TipoDocumentoActaEntrega;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoActaEntregaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoActaEntregaService {

    private static final String SUBCARPETA_ACTAS_ENTREGA = "actas-entrega";

    private final DocumentoActaEntregaRepository documentoActaEntregaRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoActaEntregaService(DocumentoActaEntregaRepository documentoActaEntregaRepository,
                                        ActaEntregaRepository actaEntregaRepository,
                                        AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoActaEntregaRepository = documentoActaEntregaRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoActaEntregaResponse agregar(UUID actaEntregaId, DocumentoActaEntregaRequest request) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        DocumentoActaEntrega documento = DocumentoActaEntrega.builder()
                .actaEntrega(acta)
                .tipo(TipoDocumentoActaEntrega.valueOf(request.getTipo()))
                .url(request.getUrl())
                .build();

        DocumentoActaEntrega guardado = documentoActaEntregaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public DocumentoActaEntregaResponse subir(UUID actaEntregaId, String tipo, MultipartFile archivo) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_ACTAS_ENTREGA, archivo);

        DocumentoActaEntrega documento = DocumentoActaEntrega.builder()
                .actaEntrega(acta)
                .tipo(TipoDocumentoActaEntrega.valueOf(tipo))
                .url(rutaPublica)
                .build();

        DocumentoActaEntrega guardado = documentoActaEntregaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoActaEntregaResponse> listarPorActa(UUID actaEntregaId) {
        return documentoActaEntregaRepository.findByActaEntregaId(actaEntregaId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoActaEntrega documento = documentoActaEntregaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoActaEntregaRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoActaEntregaResponse mapearAResponse(DocumentoActaEntrega documento) {
        return DocumentoActaEntregaResponse.builder()
                .id(documento.getId())
                .tipo(documento.getTipo().name())
                .url(documento.getUrl())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
