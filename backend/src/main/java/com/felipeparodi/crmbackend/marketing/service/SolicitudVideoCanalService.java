package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.AsignarCanalesRequest;
import com.felipeparodi.crmbackend.marketing.dto.CanalPublicacionVideoResponse;
import com.felipeparodi.crmbackend.marketing.entity.CanalPublicacionVideo;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoCanal;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.CanalPublicacionVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoCanalRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudVideoCanalService {

    private final SolicitudVideoCanalRepository solicitudVideoCanalRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final CanalPublicacionVideoRepository canalPublicacionVideoRepository;

    public SolicitudVideoCanalService(SolicitudVideoCanalRepository solicitudVideoCanalRepository,
                                       SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                       CanalPublicacionVideoRepository canalPublicacionVideoRepository) {
        this.solicitudVideoCanalRepository = solicitudVideoCanalRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.canalPublicacionVideoRepository = canalPublicacionVideoRepository;
    }

    public List<CanalPublicacionVideoResponse> asignar(UUID solicitudVideoId, AsignarCanalesRequest request) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        List<SolicitudVideoCanal> yaAsignados = solicitudVideoCanalRepository.findBySolicitudVideoId(solicitudVideoId);

        for (UUID canalId : request.getCanalIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(sc -> sc.getCanalPublicacionVideo().getId().equals(canalId));

            if (yaAsignado) {
                continue;
            }

            CanalPublicacionVideo canal = canalPublicacionVideoRepository.findById(canalId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Canal no encontrado con id: " + canalId));

            SolicitudVideoCanal nuevaAsignacion = SolicitudVideoCanal.builder()
                    .solicitudVideo(solicitud)
                    .canalPublicacionVideo(canal)
                    .build();

            solicitudVideoCanalRepository.save(nuevaAsignacion);
        }

        return listarPorSolicitud(solicitudVideoId);
    }

    public List<CanalPublicacionVideoResponse> listarPorSolicitud(UUID solicitudVideoId) {
        return solicitudVideoCanalRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(sc -> CanalPublicacionVideoResponse.builder()
                        .id(sc.getCanalPublicacionVideo().getId())
                        .nombre(sc.getCanalPublicacionVideo().getNombre())
                        .build())
                .toList();
    }
}
