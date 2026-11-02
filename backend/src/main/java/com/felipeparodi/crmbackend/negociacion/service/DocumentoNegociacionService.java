package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.negociacion.dto.DocumentoNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.DocumentoNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.TipoDocumentoNegociacion;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.DocumentoNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class DocumentoNegociacionService {

    private static final String SUBCARPETA_NEGOCIACIONES = "negociaciones";

    private final DocumentoNegociacionRepository documentoNegociacionRepository;
    private final NegociacionRepository negociacionRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoNegociacionService(DocumentoNegociacionRepository documentoNegociacionRepository,
                                        NegociacionRepository negociacionRepository,
                                        AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoNegociacionRepository = documentoNegociacionRepository;
        this.negociacionRepository = negociacionRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoNegociacionResponse subir(UUID negociacionId, String tipo, MultipartFile archivo) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + negociacionId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_NEGOCIACIONES, archivo);

        DocumentoNegociacion documento = DocumentoNegociacion.builder()
                .negociacion(negociacion)
                .tipo(TipoDocumentoNegociacion.valueOf(tipo))
                .url(rutaPublica)
                .build();

        DocumentoNegociacion guardado = documentoNegociacionRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoNegociacionResponse> listarPorNegociacion(UUID negociacionId) {
        return documentoNegociacionRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoNegociacion documento = documentoNegociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoNegociacionRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoNegociacionResponse mapearAResponse(DocumentoNegociacion documento) {
        return DocumentoNegociacionResponse.builder()
                .id(documento.getId())
                .negociacionId(documento.getNegociacion().getId())
                .tipo(documento.getTipo().name())
                .url(documento.getUrl())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
