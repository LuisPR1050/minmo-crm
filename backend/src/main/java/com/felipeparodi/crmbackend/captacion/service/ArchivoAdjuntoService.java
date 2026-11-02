package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.ArchivoAdjuntoRequest;
import com.felipeparodi.crmbackend.captacion.dto.ArchivoAdjuntoResponse;
import com.felipeparodi.crmbackend.captacion.entity.ArchivoAdjunto;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.ArchivoAdjuntoRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ArchivoAdjuntoService {

    private static final String TIPO_FOTO = "FOTO";
    private static final String SUBCARPETA_INMUEBLES = "inmuebles";

    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public ArchivoAdjuntoService(ArchivoAdjuntoRepository archivoAdjuntoRepository,
                                  InmuebleRepository inmuebleRepository,
                                  AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public ArchivoAdjuntoResponse crear(ArchivoAdjuntoRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        ArchivoAdjunto archivo = ArchivoAdjunto.builder()
                .inmueble(inmueble)
                .tipo(request.getTipo())
                .url(request.getUrl())
                .esPrincipal(request.isEsPrincipal())
                .build();

        ArchivoAdjunto guardado = archivoAdjuntoRepository.save(archivo);

        return mapearAResponse(guardado);
    }

    public ArchivoAdjuntoResponse subirFoto(UUID inmuebleId, MultipartFile foto, boolean esPrincipal) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen");
        }

        String rutaPublica = almacenamientoArchivosService.guardarImagen(SUBCARPETA_INMUEBLES, foto);

        boolean marcarComoPrincipal = esPrincipal || archivoAdjuntoRepository.findByInmuebleId(inmuebleId).isEmpty();

        if (marcarComoPrincipal) {
            desmarcarPrincipalesDe(inmuebleId);
        }

        ArchivoAdjunto archivo = ArchivoAdjunto.builder()
                .inmueble(inmueble)
                .tipo(TIPO_FOTO)
                .url(rutaPublica)
                .esPrincipal(marcarComoPrincipal)
                .build();

        ArchivoAdjunto guardado = archivoAdjuntoRepository.save(archivo);

        return mapearAResponse(guardado);
    }

    /**
     * Documentos legales del inmueble (escritura, certificado de tradicion y libertad, pago del
     * predial, etc.), reutilizando la misma tabla de archivos adjuntos que las fotos pero con un
     * "tipo" distinto para poder filtrarlos aparte en el frontend.
     */
    public ArchivoAdjuntoResponse subirDocumento(UUID inmuebleId, MultipartFile documento, String tipoDocumento) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        if (documento == null || documento.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo");
        }
        if (tipoDocumento == null || tipoDocumento.isBlank()) {
            throw new IllegalArgumentException("Debes indicar el tipo de documento");
        }

        String rutaPublica = almacenamientoArchivosService.guardarDocumento(SUBCARPETA_INMUEBLES, documento);

        ArchivoAdjunto archivo = ArchivoAdjunto.builder()
                .inmueble(inmueble)
                .tipo(tipoDocumento)
                .url(rutaPublica)
                .esPrincipal(false)
                .build();

        ArchivoAdjunto guardado = archivoAdjuntoRepository.save(archivo);

        return mapearAResponse(guardado);
    }

    public List<ArchivoAdjuntoResponse> listarTodos() {
        return archivoAdjuntoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ArchivoAdjuntoResponse> listarPorInmueble(UUID inmuebleId) {
        return archivoAdjuntoRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ArchivoAdjuntoResponse buscarPorId(UUID id) {
        ArchivoAdjunto archivo = archivoAdjuntoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado con id: " + id));

        return mapearAResponse(archivo);
    }

    public ArchivoAdjuntoResponse marcarComoPrincipal(UUID id) {
        ArchivoAdjunto archivo = archivoAdjuntoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado con id: " + id));

        desmarcarPrincipalesDe(archivo.getInmueble().getId());

        archivo.setEsPrincipal(true);
        ArchivoAdjunto actualizado = archivoAdjuntoRepository.save(archivo);

        return mapearAResponse(actualizado);
    }

    public void eliminar(UUID id) {
        ArchivoAdjunto archivo = archivoAdjuntoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Archivo no encontrado con id: " + id));

        archivoAdjuntoRepository.delete(archivo);
        almacenamientoArchivosService.eliminarPorRutaPublica(archivo.getUrl());
    }

    private void desmarcarPrincipalesDe(UUID inmuebleId) {
        List<ArchivoAdjunto> existentes = archivoAdjuntoRepository.findByInmuebleId(inmuebleId);
        existentes.forEach(a -> a.setEsPrincipal(false));
        archivoAdjuntoRepository.saveAll(existentes);
    }

    private ArchivoAdjuntoResponse mapearAResponse(ArchivoAdjunto archivo) {
        return ArchivoAdjuntoResponse.builder()
                .id(archivo.getId())
                .inmuebleId(archivo.getInmueble().getId())
                .tipo(archivo.getTipo())
                .url(archivo.getUrl())
                .esPrincipal(archivo.isEsPrincipal())
                .createdAt(archivo.getCreatedAt())
                .build();
    }
}
