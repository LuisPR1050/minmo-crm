package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.AsignarTiposPublicoRequest;
import com.felipeparodi.crmbackend.marketing.dto.TipoPublicoVideoResponse;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoTipoPublico;
import com.felipeparodi.crmbackend.marketing.entity.TipoPublicoVideo;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoTipoPublicoRepository;
import com.felipeparodi.crmbackend.marketing.repository.TipoPublicoVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudVideoTipoPublicoService {

    private final SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final TipoPublicoVideoRepository tipoPublicoVideoRepository;

    public SolicitudVideoTipoPublicoService(SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository,
                                             SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                             TipoPublicoVideoRepository tipoPublicoVideoRepository) {
        this.solicitudVideoTipoPublicoRepository = solicitudVideoTipoPublicoRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.tipoPublicoVideoRepository = tipoPublicoVideoRepository;
    }

    public List<TipoPublicoVideoResponse> asignar(UUID solicitudVideoId, AsignarTiposPublicoRequest request) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        List<SolicitudVideoTipoPublico> yaAsignados = solicitudVideoTipoPublicoRepository
                .findBySolicitudVideoId(solicitudVideoId);

        for (UUID tipoPublicoId : request.getTipoPublicoIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(st -> st.getTipoPublicoVideo().getId().equals(tipoPublicoId));

            if (yaAsignado) {
                continue;
            }

            TipoPublicoVideo tipoPublico = tipoPublicoVideoRepository.findById(tipoPublicoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Tipo de publico no encontrado con id: " + tipoPublicoId));

            SolicitudVideoTipoPublico nuevaAsignacion = SolicitudVideoTipoPublico.builder()
                    .solicitudVideo(solicitud)
                    .tipoPublicoVideo(tipoPublico)
                    .build();

            solicitudVideoTipoPublicoRepository.save(nuevaAsignacion);
        }

        return listarPorSolicitud(solicitudVideoId);
    }

    public List<TipoPublicoVideoResponse> listarPorSolicitud(UUID solicitudVideoId) {
        return solicitudVideoTipoPublicoRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(st -> TipoPublicoVideoResponse.builder()
                        .id(st.getTipoPublicoVideo().getId())
                        .nombre(st.getTipoPublicoVideo().getNombre())
                        .build())
                .toList();
    }
}
