package com.felipeparodi.crmbackend.captacion.publico.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.RequerimientoClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.service.ClienteService;
import com.felipeparodi.crmbackend.atencioncliente.service.RequerimientoClienteService;
import com.felipeparodi.crmbackend.captacion.entity.ArchivoAdjunto;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.OrigenVideo;
import com.felipeparodi.crmbackend.captacion.entity.VideoInmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingFotoResponse;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingInmuebleResponse;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingLeadRequest;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingVideoResponse;
import com.felipeparodi.crmbackend.captacion.repository.ArchivoAdjuntoRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.VideoInmuebleRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.config.AlmacenamientoVideoService;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class LandingInmuebleService {

    private final InmuebleRepository inmuebleRepository;
    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;
    private final VideoInmuebleRepository videoInmuebleRepository;
    private final InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;
    private final AlmacenamientoVideoService almacenamientoVideoService;
    private final ClienteService clienteService;
    private final RequerimientoClienteService requerimientoClienteService;

    public LandingInmuebleService(InmuebleRepository inmuebleRepository,
                                   ArchivoAdjuntoRepository archivoAdjuntoRepository,
                                   VideoInmuebleRepository videoInmuebleRepository,
                                   InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository,
                                   AlmacenamientoArchivosService almacenamientoArchivosService,
                                   AlmacenamientoVideoService almacenamientoVideoService,
                                   ClienteService clienteService,
                                   RequerimientoClienteService requerimientoClienteService) {
        this.inmuebleRepository = inmuebleRepository;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.videoInmuebleRepository = videoInmuebleRepository;
        this.inmuebleCaracteristicaRepository = inmuebleCaracteristicaRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.almacenamientoVideoService = almacenamientoVideoService;
        this.clienteService = clienteService;
        this.requerimientoClienteService = requerimientoClienteService;
    }

    public LandingInmuebleResponse obtener(UUID inmuebleId) {
        Inmueble inmueble = obtenerInmuebleConLandingActiva(inmuebleId);

        List<LandingFotoResponse> fotos = archivoAdjuntoRepository.findByInmuebleId(inmuebleId).stream()
                .sorted(Comparator.comparing(ArchivoAdjunto::isEsPrincipal).reversed()
                        .thenComparing(ArchivoAdjunto::getCreatedAt))
                .map(foto -> LandingFotoResponse.builder()
                        .id(foto.getId())
                        .url("/api/public/inmuebles/" + inmuebleId + "/fotos/" + foto.getId())
                        .esPrincipal(foto.isEsPrincipal())
                        .build())
                .toList();

        List<LandingVideoResponse> videos = videoInmuebleRepository.findByInmuebleId(inmuebleId).stream()
                .map(video -> LandingVideoResponse.builder()
                        .id(video.getId())
                        .url(urlPublicaDeVideo(inmuebleId, video))
                        .origen(video.getOrigen().name())
                        .build())
                .toList();

        return LandingInmuebleResponse.builder()
                .id(inmueble.getId())
                .codigo(inmueble.getCodigo())
                .tipoPropiedad(inmueble.getTipoPropiedad())
                .ciudad(inmueble.getCiudad())
                .zona(inmueble.getZona())
                .entorno(inmueble.getEntorno())
                .areaTotal(inmueble.getAreaTotal())
                .areaConstruida(inmueble.getAreaConstruida())
                .areaPrivada(inmueble.getAreaPrivada())
                .numeroHabitaciones(inmueble.getNumeroHabitaciones())
                .numeroBanos(inmueble.getNumeroBanos())
                .piso(inmueble.getPiso())
                .niveles(inmueble.getNiveles())
                .vista(inmueble.getVista())
                .estratoSocioeconomico(inmueble.getEstratoSocioeconomico())
                .precio(inmueble.isLandingMostrarPrecio() ? inmueble.getExpectativaPrecio() : null)
                .descripcionPublica(inmueble.getDescripcionPublica())
                .ganchoEmocional(inmueble.getGanchoEmocional())
                .fotos(fotos)
                .videos(videos)
                .caracteristicas(inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId).stream()
                        .map(ic -> ic.getCaracteristica().getNombre())
                        .sorted()
                        .toList())
                .build();
    }

    public void crearLead(UUID inmuebleId, LandingLeadRequest request) {
        Inmueble inmueble = obtenerInmuebleConLandingActiva(inmuebleId);

        ClienteRequest clienteRequest = new ClienteRequest();
        clienteRequest.setNombre(request.getNombre());
        clienteRequest.setTelefono(request.getTelefono());
        clienteRequest.setCorreoElectronico(request.getCorreoElectronico());
        clienteRequest.setTipoContacto("Landing pública");
        clienteRequest.setSubtipoContacto(inmueble.getCodigo());

        var clienteCreado = clienteService.crear(clienteRequest);

        if (request.getMensaje() != null && !request.getMensaje().isBlank()) {
            RequerimientoClienteRequest requerimientoRequest = new RequerimientoClienteRequest();
            requerimientoRequest.setClienteId(clienteCreado.getId());
            requerimientoRequest.setObservaciones(
                    "Interesado en " + inmueble.getCodigo() + ". Mensaje del visitante: " + request.getMensaje());
            requerimientoClienteService.crear(requerimientoRequest);
        }
    }

    public byte[] leerFoto(UUID inmuebleId, UUID fotoId) {
        obtenerInmuebleConLandingActiva(inmuebleId);

        ArchivoAdjunto foto = archivoAdjuntoRepository.findById(fotoId)
                .filter(a -> a.getInmueble().getId().equals(inmuebleId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Foto no encontrada con id: " + fotoId));

        return almacenamientoArchivosService.leerBytes(foto.getUrl());
    }

    public MediaType tipoContenidoDeFoto(UUID inmuebleId, UUID fotoId) {
        ArchivoAdjunto foto = archivoAdjuntoRepository.findById(fotoId)
                .filter(a -> a.getInmueble().getId().equals(inmuebleId))
                .orElseThrow(() -> new RecursoNoEncontradoException("Foto no encontrada con id: " + fotoId));

        String url = foto.getUrl().toLowerCase(Locale.ROOT);
        if (url.endsWith(".png")) return MediaType.IMAGE_PNG;
        if (url.endsWith(".webp")) return MediaType.valueOf("image/webp");
        return MediaType.IMAGE_JPEG;
    }

    public Resource obtenerRecursoVideo(UUID inmuebleId, UUID videoId) {
        obtenerInmuebleConLandingActiva(inmuebleId);

        VideoInmueble video = buscarVideoSubidoDelInmueble(inmuebleId, videoId);

        return new FileSystemResource(almacenamientoVideoService.resolverRutaLocal(video.getUrl()));
    }

    public MediaType tipoContenidoDeVideo(UUID inmuebleId, UUID videoId) {
        VideoInmueble video = buscarVideoSubidoDelInmueble(inmuebleId, videoId);

        if (video.getTipoContenido() != null) {
            return MediaType.parseMediaType(video.getTipoContenido());
        }
        return MediaType.valueOf("video/mp4");
    }

    private VideoInmueble buscarVideoSubidoDelInmueble(UUID inmuebleId, UUID videoId) {
        return videoInmuebleRepository.findById(videoId)
                .filter(v -> v.getInmueble().getId().equals(inmuebleId))
                .filter(v -> v.getOrigen() == OrigenVideo.ARCHIVO_SUBIDO)
                .orElseThrow(() -> new RecursoNoEncontradoException("Video no encontrado con id: " + videoId));
    }

    private String urlPublicaDeVideo(UUID inmuebleId, VideoInmueble video) {
        if (video.getOrigen() == OrigenVideo.ARCHIVO_SUBIDO && video.getUrl().startsWith("/uploads/")) {
            return "/api/public/inmuebles/" + inmuebleId + "/videos/" + video.getId();
        }
        return video.getUrl();
    }

    private Inmueble obtenerInmuebleConLandingActiva(UUID inmuebleId) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        if (!inmueble.isLandingActiva()) {
            throw new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId);
        }

        return inmueble;
    }
}
