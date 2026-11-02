package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.DocumentoReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.entity.DocumentoReciboPago;
import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoReciboPagoRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class DocumentoReciboPagoService {

    private static final String SUBCARPETA_RECIBOS_PAGO = "recibos-pago";

    private final DocumentoReciboPagoRepository documentoReciboPagoRepository;
    private final ReciboPagoRepository reciboPagoRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public DocumentoReciboPagoService(DocumentoReciboPagoRepository documentoReciboPagoRepository,
                                       ReciboPagoRepository reciboPagoRepository,
                                       AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.documentoReciboPagoRepository = documentoReciboPagoRepository;
        this.reciboPagoRepository = reciboPagoRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public DocumentoReciboPagoResponse agregar(UUID reciboPagoId, DocumentoReciboPagoRequest request) {
        ReciboPago recibo = reciboPagoRepository.findById(reciboPagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recibo de pago no encontrado con id: " + reciboPagoId));

        DocumentoReciboPago documento = DocumentoReciboPago.builder()
                .reciboPago(recibo)
                .url(request.getUrl())
                .build();

        DocumentoReciboPago guardado = documentoReciboPagoRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public DocumentoReciboPagoResponse subir(UUID reciboPagoId, MultipartFile archivo) {
        ReciboPago recibo = reciboPagoRepository.findById(reciboPagoId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recibo de pago no encontrado con id: " + reciboPagoId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_RECIBOS_PAGO, archivo);

        DocumentoReciboPago documento = DocumentoReciboPago.builder()
                .reciboPago(recibo)
                .url(rutaPublica)
                .build();

        DocumentoReciboPago guardado = documentoReciboPagoRepository.save(documento);

        return mapearAResponse(guardado);
    }

    public List<DocumentoReciboPagoResponse> listarPorRecibo(UUID reciboPagoId) {
        return documentoReciboPagoRepository.findByReciboPagoId(reciboPagoId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        DocumentoReciboPago documento = documentoReciboPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Documento no encontrado con id: " + id));

        documentoReciboPagoRepository.delete(documento);
        almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl());
    }

    private DocumentoReciboPagoResponse mapearAResponse(DocumentoReciboPago documento) {
        return DocumentoReciboPagoResponse.builder()
                .id(documento.getId())
                .url(documento.getUrl())
                .createdAt(documento.getCreatedAt())
                .build();
    }
}
