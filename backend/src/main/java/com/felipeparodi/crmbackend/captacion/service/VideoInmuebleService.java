package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.VideoInmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.VideoInmuebleResponse;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.OrigenVideo;
import com.felipeparodi.crmbackend.captacion.entity.VideoInmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.VideoInmuebleRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoVideoService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class VideoInmuebleService {

    private static final String SUBCARPETA_VIDEOS = "videos-inmueble";

    private final VideoInmuebleRepository videoInmuebleRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AlmacenamientoVideoService almacenamientoVideoService;

    public VideoInmuebleService(VideoInmuebleRepository videoInmuebleRepository,
                                 InmuebleRepository inmuebleRepository,
                                 AlmacenamientoVideoService almacenamientoVideoService) {
        this.videoInmuebleRepository = videoInmuebleRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.almacenamientoVideoService = almacenamientoVideoService;
    }

    public VideoInmuebleResponse agregar(UUID inmuebleId, VideoInmuebleRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        String url = validarUrl(request.getUrl());

        VideoInmueble video = VideoInmueble.builder()
                .inmueble(inmueble)
                .url(url)
                .origen(OrigenVideo.LINK_EXTERNO)
                .build();

        VideoInmueble guardado = videoInmuebleRepository.save(video);

        return mapearAResponse(guardado);
    }

    public VideoInmuebleResponse subirArchivo(UUID inmuebleId, MultipartFile archivo) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un video");
        }

        String url = almacenamientoVideoService.guardarVideo(SUBCARPETA_VIDEOS, archivo);

        VideoInmueble video = VideoInmueble.builder()
                .inmueble(inmueble)
                .url(url)
                .origen(OrigenVideo.ARCHIVO_SUBIDO)
                .tipoContenido(archivo.getContentType())
                .build();

        VideoInmueble guardado = videoInmuebleRepository.save(video);

        return mapearAResponse(guardado);
    }

    public List<VideoInmuebleResponse> listarPorInmueble(UUID inmuebleId) {
        return videoInmuebleRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public void eliminar(UUID id) {
        VideoInmueble video = videoInmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Video no encontrado con id: " + id));

        if (video.getOrigen() == OrigenVideo.ARCHIVO_SUBIDO) {
            almacenamientoVideoService.eliminarVideo(video.getUrl());
        }

        videoInmuebleRepository.delete(video);
    }

    private String validarUrl(String url) {
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("El enlace del video es obligatorio");
        }

        String urlLimpia = url.trim();
        if (!urlLimpia.startsWith("http://") && !urlLimpia.startsWith("https://")) {
            throw new IllegalArgumentException("El enlace del video debe empezar con http:// o https://");
        }

        return urlLimpia;
    }

    private VideoInmuebleResponse mapearAResponse(VideoInmueble video) {
        return VideoInmuebleResponse.builder()
                .id(video.getId())
                .inmuebleId(video.getInmueble().getId())
                .url(video.getUrl())
                .origen(video.getOrigen().name())
                .tipoContenido(video.getTipoContenido())
                .createdAt(video.getCreatedAt())
                .build();
    }
}
