package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoOtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoOtrosiPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.DocumentoOtrosiPromesa;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoOtrosiPromesaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoOtrosiPromesaService {

    private static final String SUBCARPETA_OTROSIS_PROMESA = "otrosis-promesa";

    private final DocumentoOtrosiPromesaRepository documentoOtrosiPromesaRepository;
    private final OtrosiPromesaRepository otrosiPromesaRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoOtrosiPromesaService(DocumentoOtrosiPromesaRepository documentoOtrosiPromesaRepository,
                                          OtrosiPromesaRepository otrosiPromesaRepository,
                                          AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoOtrosiPromesaRepository = documentoOtrosiPromesaRepository;
        this.otrosiPromesaRepository = otrosiPromesaRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoOtrosiPromesaResponse agregar(UUID otrosiPromesaId, DocumentoOtrosiPromesaRequest request) {
        OtrosiPromesa otrosi = otrosiPromesaRepository.findById(otrosiPromesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Otrosi no encontrado con id: " + otrosiPromesaId));

        DocumentoOtrosiPromesa documento = DocumentoOtrosiPromesa.builder()
                .otrosiPromesa(otrosi)
                .url(request.getUrl())
                .descripcion(request.getDescripcion())
                .build();

        DocumentoOtrosiPromesa guardado = documentoOtrosiPromesaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public DocumentoOtrosiPromesaResponse subir(UUID otrosiPromesaId, String descripcion, MultipartFile archivo) {
        OtrosiPromesa otrosi = otrosiPromesaRepository.findById(otrosiPromesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Otrosi no encontrado con id: " + otrosiPromesaId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_OTROSIS_PROMESA, archivo);

        DocumentoOtrosiPromesa documento = DocumentoOtrosiPromesa.builder()
                .otrosiPromesa(otrosi)
                .url(rutaPublica)
                .descripcion(descripcion)
                .build();

        DocumentoOtrosiPromesa guardado = documentoOtrosiPromesaRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoOtrosiPromesaResponse> listarPorOtrosi(UUID otrosiPromesaId) {
        return documentoOtrosiPromesaRepository.findByOtrosiPromesaId(otrosiPromesaId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoOtrosiPromesa documento = documentoOtrosiPromesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoOtrosiPromesaRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoOtrosiPromesaResponse mapearAResponse(DocumentoOtrosiPromesa documento) {
        return DocumentoOtrosiPromesaResponse.builder()
                .id(documento.getId())
                .url(documento.getUrl())
                .descripcion(documento.getDescripcion())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
