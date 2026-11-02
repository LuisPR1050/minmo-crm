package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.marketing.dto.FotoSolicitudVideoRequest;
import com.felipeparodi.crmbackend.marketing.dto.FotoSolicitudVideoResponse;
import com.felipeparodi.crmbackend.marketing.entity.FotoSolicitudVideo;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.FotoSolicitudVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FotoSolicitudVideoService {

    private static final String SUBCARPETA_SOLICITUDES_VIDEO = "solicitudes-video";

    private final FotoSolicitudVideoRepository fotoSolicitudVideoRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public FotoSolicitudVideoService(FotoSolicitudVideoRepository fotoSolicitudVideoRepository,
                                      SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                      AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.fotoSolicitudVideoRepository = fotoSolicitudVideoRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public FotoSolicitudVideoResponse agregar(UUID solicitudVideoId, FotoSolicitudVideoRequest request) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        FotoSolicitudVideo foto = FotoSolicitudVideo.builder()
                .solicitudVideo(solicitud)
                .url(request.getUrl())
                .build();

        FotoSolicitudVideo guardada = fotoSolicitudVideoRepository.save(foto);

        return mapearAResponse(guardada);
    }

    public FotoSolicitudVideoResponse subir(UUID solicitudVideoId, MultipartFile foto) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen");
        }

        String rutaPublica = almacenamientoArchivosService.guardarImagen(SUBCARPETA_SOLICITUDES_VIDEO, foto);

        FotoSolicitudVideo nuevaFoto = FotoSolicitudVideo.builder()
                .solicitudVideo(solicitud)
                .url(rutaPublica)
                .build();

        FotoSolicitudVideo guardada = fotoSolicitudVideoRepository.save(nuevaFoto);

        return mapearAResponse(guardada);
    }

    public List<FotoSolicitudVideoResponse> listarPorSolicitud(UUID solicitudVideoId) {
        return fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private FotoSolicitudVideoResponse mapearAResponse(FotoSolicitudVideo foto) {
        return FotoSolicitudVideoResponse.builder()
                .id(foto.getId())
                .url(foto.getUrl())
                .createdAt(foto.getCreatedAt())
                .build();
    }
}
