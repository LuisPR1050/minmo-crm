package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.VideoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.VideoPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.entity.TipoOrigenArchivo;
import com.felipeparodi.crmbackend.marketing.entity.VideoPublicacion;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoPublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VideoPublicacionService {

    private final VideoPublicacionRepository videoPublicacionRepository;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;

    public VideoPublicacionService(VideoPublicacionRepository videoPublicacionRepository,
                                    PublicacionInmuebleRepository publicacionInmuebleRepository) {
        this.videoPublicacionRepository = videoPublicacionRepository;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
    }

    public VideoPublicacionResponse agregar(UUID publicacionInmuebleId, VideoPublicacionRequest request) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionInmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionInmuebleId));

        VideoPublicacion video = VideoPublicacion.builder()
                .publicacionInmueble(publicacion)
                .url(request.getUrl())
                .tipoOrigen(TipoOrigenArchivo.valueOf(request.getTipoOrigen()))
                .build();

        VideoPublicacion guardado = videoPublicacionRepository.save(video);

        return mapearAResponse(guardado);
    }

    public List<VideoPublicacionResponse> listarPorPublicacion(UUID publicacionInmuebleId) {
        return videoPublicacionRepository.findByPublicacionInmuebleId(publicacionInmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private VideoPublicacionResponse mapearAResponse(VideoPublicacion video) {
        return VideoPublicacionResponse.builder()
                .id(video.getId())
                .url(video.getUrl())
                .tipoOrigen(video.getTipoOrigen().name())
                .createdAt(video.getCreatedAt())
                .build();
    }
}
