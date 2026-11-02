package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.config.AlmacenamientoVideoService;
import com.felipeparodi.crmbackend.marketing.dto.ActualizarSolicitudVideoMarketingRequest;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoSolicitudVideoRequest;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingRequest;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingResponse;
import com.felipeparodi.crmbackend.marketing.entity.EstadoSolicitudVideo;
import com.felipeparodi.crmbackend.marketing.entity.EstiloContenidoVideo;
import com.felipeparodi.crmbackend.marketing.entity.LlamadoAccionVideo;
import com.felipeparodi.crmbackend.marketing.entity.ObjetivoVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.CopiaVideoCanalRepository;
import com.felipeparodi.crmbackend.marketing.repository.FotoSolicitudVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoCanalRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoDestinoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoTipoPublicoRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoFuenteSolicitudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class SolicitudVideoMarketingService {

    private static final String SUBCARPETA_LOGOS = "solicitudes-video/logos";
    private static final String SUBCARPETA_MUSICA = "solicitudes-video/musica";

    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;
    private final AlmacenamientoVideoService almacenamientoVideoService;
    private final CopiaVideoCanalRepository copiaVideoCanalRepository;
    private final FotoSolicitudVideoRepository fotoSolicitudVideoRepository;
    private final SolicitudVideoCanalRepository solicitudVideoCanalRepository;
    private final SolicitudVideoDestinoRepository solicitudVideoDestinoRepository;
    private final SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository;
    private final VideoFuenteSolicitudRepository videoFuenteSolicitudRepository;

    public SolicitudVideoMarketingService(SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                           InmuebleRepository inmuebleRepository,
                                           AlmacenamientoArchivosService almacenamientoArchivosService,
                                           AlmacenamientoVideoService almacenamientoVideoService,
                                           CopiaVideoCanalRepository copiaVideoCanalRepository,
                                           FotoSolicitudVideoRepository fotoSolicitudVideoRepository,
                                           SolicitudVideoCanalRepository solicitudVideoCanalRepository,
                                           SolicitudVideoDestinoRepository solicitudVideoDestinoRepository,
                                           SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository,
                                           VideoFuenteSolicitudRepository videoFuenteSolicitudRepository) {
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.almacenamientoVideoService = almacenamientoVideoService;
        this.copiaVideoCanalRepository = copiaVideoCanalRepository;
        this.fotoSolicitudVideoRepository = fotoSolicitudVideoRepository;
        this.solicitudVideoCanalRepository = solicitudVideoCanalRepository;
        this.solicitudVideoDestinoRepository = solicitudVideoDestinoRepository;
        this.solicitudVideoTipoPublicoRepository = solicitudVideoTipoPublicoRepository;
        this.videoFuenteSolicitudRepository = videoFuenteSolicitudRepository;
    }

    public SolicitudVideoMarketingResponse crear(SolicitudVideoMarketingRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        if (request.getObjetivoPublicacion() == null) {
            throw new IllegalArgumentException("El objetivo de la publicacion es obligatorio");
        }

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder()
                .inmueble(inmueble)
                .objetivoPublicacion(ObjetivoVideoMarketing.valueOf(request.getObjetivoPublicacion()))
                .estiloContenido(request.getEstiloContenido() != null
                        ? EstiloContenidoVideo.valueOf(request.getEstiloContenido()) : null)
                .duracionMinimaMinutos(request.getDuracionMinimaMinutos())
                .duracionMaximaMinutos(request.getDuracionMaximaMinutos())
                .llamadoAccion(request.getLlamadoAccion() != null
                        ? LlamadoAccionVideo.valueOf(request.getLlamadoAccion()) : null)
                .logoUrl(request.getLogoUrl())
                .build();

        SolicitudVideoMarketing guardada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(guardada);
    }

    public List<SolicitudVideoMarketingResponse> listarTodas() {
        return solicitudVideoMarketingRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<SolicitudVideoMarketingResponse> listarPorInmueble(UUID inmuebleId) {
        return solicitudVideoMarketingRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public SolicitudVideoMarketingResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerSolicitud(id));
    }

    public void eliminar(UUID id) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        if (solicitud.getLogoUrl() != null) {
            almacenamientoArchivosService.eliminarPorRutaPublica(solicitud.getLogoUrl());
        }
        if (solicitud.getMusicaFondoUrl() != null) {
            almacenamientoVideoService.eliminarVideo(solicitud.getMusicaFondoUrl());
        }

        var fotos = fotoSolicitudVideoRepository.findBySolicitudVideoId(id);
        fotos.forEach(foto -> almacenamientoArchivosService.eliminarPorRutaPublica(foto.getUrl()));
        fotoSolicitudVideoRepository.deleteAll(fotos);
        copiaVideoCanalRepository.deleteBySolicitudVideoId(id);
        solicitudVideoCanalRepository.deleteAll(solicitudVideoCanalRepository.findBySolicitudVideoId(id));
        solicitudVideoDestinoRepository.deleteAll(solicitudVideoDestinoRepository.findBySolicitudVideoId(id));
        solicitudVideoTipoPublicoRepository.deleteAll(solicitudVideoTipoPublicoRepository.findBySolicitudVideoId(id));
        videoFuenteSolicitudRepository.deleteAll(videoFuenteSolicitudRepository.findBySolicitudVideoId(id));

        solicitudVideoMarketingRepository.delete(solicitud);
    }

    public SolicitudVideoMarketingResponse actualizar(UUID id, ActualizarSolicitudVideoMarketingRequest request) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        if (request.getObjetivoPublicacion() != null) {
            solicitud.setObjetivoPublicacion(ObjetivoVideoMarketing.valueOf(request.getObjetivoPublicacion()));
        }
        if (request.getEstiloContenido() != null) {
            solicitud.setEstiloContenido(EstiloContenidoVideo.valueOf(request.getEstiloContenido()));
        }
        if (request.getDuracionMinimaMinutos() != null) {
            solicitud.setDuracionMinimaMinutos(request.getDuracionMinimaMinutos());
        }
        if (request.getDuracionMaximaMinutos() != null) {
            solicitud.setDuracionMaximaMinutos(request.getDuracionMaximaMinutos());
        }
        if (request.getLlamadoAccion() != null) {
            solicitud.setLlamadoAccion(LlamadoAccionVideo.valueOf(request.getLlamadoAccion()));
        }
        if (request.getLogoUrl() != null) {
            solicitud.setLogoUrl(request.getLogoUrl());
        }

        SolicitudVideoMarketing actualizada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(actualizada);
    }

    public SolicitudVideoMarketingResponse subirLogo(UUID id, MultipartFile logo) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        if (logo == null || logo.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen");
        }

        String rutaPublica = almacenamientoArchivosService.guardarImagen(SUBCARPETA_LOGOS, logo);
        solicitud.setLogoUrl(rutaPublica);

        SolicitudVideoMarketing actualizada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(actualizada);
    }

    /**
     * Sube un archivo de audio del dispositivo del usuario para usarlo como musica de fondo del
     * video de esta solicitud, en vez del tono ambiental por defecto. Si ya habia una musica
     * subida antes, se borra la anterior (no se acumulan archivos huerfanos).
     */
    public SolicitudVideoMarketingResponse subirMusica(UUID id, MultipartFile musica) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        if (musica == null || musica.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar un archivo de audio");
        }

        if (solicitud.getMusicaFondoUrl() != null) {
            almacenamientoVideoService.eliminarVideo(solicitud.getMusicaFondoUrl());
        }

        String rutaPublica = almacenamientoVideoService.guardarAudio(SUBCARPETA_MUSICA, musica);
        solicitud.setMusicaFondoUrl(rutaPublica);

        SolicitudVideoMarketing actualizada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(actualizada);
    }

    /**
     * Quita la musica de fondo personalizada; el video vuelve a generarse con el tono ambiental
     * por defecto (o con ffmpeg.musica-fondo-path si el servidor tiene una pista configurada).
     */
    public SolicitudVideoMarketingResponse eliminarMusica(UUID id) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        if (solicitud.getMusicaFondoUrl() != null) {
            almacenamientoVideoService.eliminarVideo(solicitud.getMusicaFondoUrl());
            solicitud.setMusicaFondoUrl(null);
        }

        SolicitudVideoMarketing actualizada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(actualizada);
    }

    public SolicitudVideoMarketingResponse cambiarEstado(UUID id, CambiarEstadoSolicitudVideoRequest request) {
        SolicitudVideoMarketing solicitud = obtenerSolicitud(id);

        solicitud.setEstado(EstadoSolicitudVideo.valueOf(request.getNuevoEstado()));

        SolicitudVideoMarketing actualizada = solicitudVideoMarketingRepository.save(solicitud);

        return mapearAResponse(actualizada);
    }

    private SolicitudVideoMarketing obtenerSolicitud(UUID id) {
        return solicitudVideoMarketingRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de video no encontrada con id: " + id));
    }

    private SolicitudVideoMarketingResponse mapearAResponse(SolicitudVideoMarketing solicitud) {
        return SolicitudVideoMarketingResponse.builder()
                .id(solicitud.getId())
                .inmuebleId(solicitud.getInmueble().getId())
                .inmuebleCodigo(solicitud.getInmueble().getCodigo())
                .objetivoPublicacion(solicitud.getObjetivoPublicacion() != null
                        ? solicitud.getObjetivoPublicacion().name() : null)
                .estiloContenido(solicitud.getEstiloContenido() != null ? solicitud.getEstiloContenido().name() : null)
                .duracionMinimaMinutos(solicitud.getDuracionMinimaMinutos())
                .duracionMaximaMinutos(solicitud.getDuracionMaximaMinutos())
                .llamadoAccion(solicitud.getLlamadoAccion() != null ? solicitud.getLlamadoAccion().name() : null)
                .logoUrl(solicitud.getLogoUrl())
                .musicaFondoUrl(solicitud.getMusicaFondoUrl())
                .estado(solicitud.getEstado().name())
                .videoGeneradoUrl(solicitud.getVideoGeneradoUrl())
                .tituloGenerado(solicitud.getTituloGenerado())
                .subtituloGenerado(solicitud.getSubtituloGenerado())
                .descripcionGenerada(solicitud.getDescripcionGenerada())
                .llamadoAccionTexto(solicitud.getLlamadoAccionTexto())
                .hashtags(solicitud.getHashtags())
                .fechaGeneracionCopy(solicitud.getFechaGeneracionCopy())
                .createdAt(solicitud.getCreatedAt())
                .build();
    }
}
