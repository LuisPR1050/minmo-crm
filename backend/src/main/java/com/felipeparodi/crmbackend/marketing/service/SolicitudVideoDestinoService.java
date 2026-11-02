package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.AsignarDestinosRequest;
import com.felipeparodi.crmbackend.marketing.dto.DestinoVideoResponse;
import com.felipeparodi.crmbackend.marketing.entity.DestinoVideo;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoDestino;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.DestinoVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoDestinoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudVideoDestinoService {

    private final SolicitudVideoDestinoRepository solicitudVideoDestinoRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final DestinoVideoRepository destinoVideoRepository;

    public SolicitudVideoDestinoService(SolicitudVideoDestinoRepository solicitudVideoDestinoRepository,
                                         SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                         DestinoVideoRepository destinoVideoRepository) {
        this.solicitudVideoDestinoRepository = solicitudVideoDestinoRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.destinoVideoRepository = destinoVideoRepository;
    }

    public List<DestinoVideoResponse> asignar(UUID solicitudVideoId, AsignarDestinosRequest request) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        List<SolicitudVideoDestino> yaAsignados = solicitudVideoDestinoRepository.findBySolicitudVideoId(solicitudVideoId);

        for (UUID destinoId : request.getDestinoIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(sd -> sd.getDestinoVideo().getId().equals(destinoId));

            if (yaAsignado) {
                continue;
            }

            DestinoVideo destino = destinoVideoRepository.findById(destinoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Destino no encontrado con id: " + destinoId));

            SolicitudVideoDestino nuevaAsignacion = SolicitudVideoDestino.builder()
                    .solicitudVideo(solicitud)
                    .destinoVideo(destino)
                    .build();

            solicitudVideoDestinoRepository.save(nuevaAsignacion);
        }

        return listarPorSolicitud(solicitudVideoId);
    }

    public List<DestinoVideoResponse> listarPorSolicitud(UUID solicitudVideoId) {
        return solicitudVideoDestinoRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(sd -> DestinoVideoResponse.builder()
                        .id(sd.getDestinoVideo().getId())
                        .nombre(sd.getDestinoVideo().getNombre())
                        .build())
                .toList();
    }
}
