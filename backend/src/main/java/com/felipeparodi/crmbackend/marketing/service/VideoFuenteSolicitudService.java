package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.VideoFuenteSolicitudRequest;
import com.felipeparodi.crmbackend.marketing.dto.VideoFuenteSolicitudResponse;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.TipoOrigenArchivo;
import com.felipeparodi.crmbackend.marketing.entity.VideoFuenteSolicitud;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoFuenteSolicitudRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VideoFuenteSolicitudService {

    private final VideoFuenteSolicitudRepository videoFuenteSolicitudRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;

    public VideoFuenteSolicitudService(VideoFuenteSolicitudRepository videoFuenteSolicitudRepository,
                                        SolicitudVideoMarketingRepository solicitudVideoMarketingRepository) {
        this.videoFuenteSolicitudRepository = videoFuenteSolicitudRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
    }

    public VideoFuenteSolicitudResponse agregar(UUID solicitudVideoId, VideoFuenteSolicitudRequest request) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        VideoFuenteSolicitud video = VideoFuenteSolicitud.builder()
                .solicitudVideo(solicitud)
                .url(request.getUrl())
                .tipoOrigen(TipoOrigenArchivo.valueOf(request.getTipoOrigen()))
                .build();

        VideoFuenteSolicitud guardado = videoFuenteSolicitudRepository.save(video);

        return mapearAResponse(guardado);
    }

    public List<VideoFuenteSolicitudResponse> listarPorSolicitud(UUID solicitudVideoId) {
        return videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private VideoFuenteSolicitudResponse mapearAResponse(VideoFuenteSolicitud video) {
        return VideoFuenteSolicitudResponse.builder()
                .id(video.getId())
                .url(video.getUrl())
                .tipoOrigen(video.getTipoOrigen().name())
                .createdAt(video.getCreatedAt())
                .build();
    }
}
