package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.DocumentoContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.TipoDocumentoContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoContratoCorretajeRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoContratoCorretajeService {

    private static final String SUBCARPETA_CONTRATOS_CORRETAJE = "contratos-corretaje";

    private final DocumentoContratoCorretajeRepository documentoContratoCorretajeRepository;
    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoContratoCorretajeService(DocumentoContratoCorretajeRepository documentoContratoCorretajeRepository,
                                              ContratoCorretajeRepository contratoCorretajeRepository,
                                              AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoContratoCorretajeRepository = documentoContratoCorretajeRepository;
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoContratoCorretajeResponse agregar(UUID contratoCorretajeId, DocumentoContratoCorretajeRequest request) {
        ContratoCorretaje contrato = contratoCorretajeRepository.findById(contratoCorretajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato de corretaje no encontrado con id: " + contratoCorretajeId));

        DocumentoContratoCorretaje documento = DocumentoContratoCorretaje.builder()
                .contratoCorretaje(contrato)
                .tipo(TipoDocumentoContratoCorretaje.valueOf(request.getTipo()))
                .url(request.getUrl())
                .build();

        DocumentoContratoCorretaje guardado = documentoContratoCorretajeRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public DocumentoContratoCorretajeResponse subir(UUID contratoCorretajeId, String tipo, MultipartFile archivo) {
        ContratoCorretaje contrato = contratoCorretajeRepository.findById(contratoCorretajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato de corretaje no encontrado con id: " + contratoCorretajeId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_CONTRATOS_CORRETAJE, archivo);

        DocumentoContratoCorretaje documento = DocumentoContratoCorretaje.builder()
                .contratoCorretaje(contrato)
                .tipo(TipoDocumentoContratoCorretaje.valueOf(tipo))
                .url(rutaPublica)
                .build();

        DocumentoContratoCorretaje guardado = documentoContratoCorretajeRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoContratoCorretajeResponse> listarPorContrato(UUID contratoCorretajeId) {
        return documentoContratoCorretajeRepository.findByContratoCorretajeId(contratoCorretajeId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoContratoCorretaje documento = documentoContratoCorretajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoContratoCorretajeRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoContratoCorretajeResponse mapearAResponse(DocumentoContratoCorretaje documento) {
        return DocumentoContratoCorretajeResponse.builder()
                .id(documento.getId())
                .tipo(documento.getTipo().name())
                .url(documento.getUrl())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
