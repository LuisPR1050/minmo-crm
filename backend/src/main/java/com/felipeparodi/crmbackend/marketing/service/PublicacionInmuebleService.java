package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.marketing.dto.ActualizarPublicacionInmuebleRequest;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmuebleRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmuebleResponse;
import com.felipeparodi.crmbackend.marketing.entity.EstadoPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.entity.TipoOperacionPublicacion;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.FotoPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoPublicacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PublicacionInmuebleService {

    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final InmuebleRepository inmuebleRepository;
    private final HistorialPublicacionRepository historialPublicacionRepository;
    private final FotoPublicacionRepository fotoPublicacionRepository;
    private final PublicacionInmueblePortalRepository publicacionInmueblePortalRepository;
    private final VideoPublicacionRepository videoPublicacionRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public PublicacionInmuebleService(PublicacionInmuebleRepository publicacionInmuebleRepository,
                                       InmuebleRepository inmuebleRepository,
                                       HistorialPublicacionRepository historialPublicacionRepository,
                                       FotoPublicacionRepository fotoPublicacionRepository,
                                       PublicacionInmueblePortalRepository publicacionInmueblePortalRepository,
                                       VideoPublicacionRepository videoPublicacionRepository,
                                       AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.historialPublicacionRepository = historialPublicacionRepository;
        this.fotoPublicacionRepository = fotoPublicacionRepository;
        this.publicacionInmueblePortalRepository = publicacionInmueblePortalRepository;
        this.videoPublicacionRepository = videoPublicacionRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public PublicacionInmuebleResponse crear(PublicacionInmuebleRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        PublicacionInmueble publicacion = PublicacionInmueble.builder()
                .inmueble(inmueble)
                .tipoOperacion(request.getTipoOperacion() != null
                        ? TipoOperacionPublicacion.valueOf(request.getTipoOperacion()) : null)
                .mostrarPrecio(request.getMostrarPrecio() != null ? request.getMostrarPrecio() : true)
                .mostrarContacto(request.getMostrarContacto() != null ? request.getMostrarContacto() : true)
                .mostrarCorreo(request.getMostrarCorreo() != null ? request.getMostrarCorreo() : false)
                .comisionCompartida(request.getComisionCompartida() != null ? request.getComisionCompartida() : false)
                .destacada(request.getDestacada() != null ? request.getDestacada() : false)
                .descripcionCorta(request.getDescripcionCorta())
                .descripcionLarga(request.getDescripcionLarga())
                .descripcionOptimizada(request.getDescripcionOptimizada())
                .build();

        PublicacionInmueble guardada = publicacionInmuebleRepository.save(publicacion);

        registrarHistorial(guardada, "Publicacion creada en estado BORRADOR");

        return mapearAResponse(guardada);
    }

    public List<PublicacionInmuebleResponse> listarTodas() {
        return publicacionInmuebleRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<PublicacionInmuebleResponse> listarPorInmueble(UUID inmuebleId) {
        return publicacionInmuebleRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public PublicacionInmuebleResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerPublicacion(id));
    }

    public void eliminar(UUID id) {
        PublicacionInmueble publicacion = obtenerPublicacion(id);

        var fotos = fotoPublicacionRepository.findByPublicacionInmuebleId(id);
        fotos.forEach(foto -> almacenamientoArchivosService.eliminarPorRutaPublica(foto.getUrl()));
        fotoPublicacionRepository.deleteAll(fotos);
        publicacionInmueblePortalRepository.deleteAll(publicacionInmueblePortalRepository.findByPublicacionInmuebleId(id));
        videoPublicacionRepository.deleteAll(videoPublicacionRepository.findByPublicacionInmuebleId(id));
        historialPublicacionRepository.deleteAll(historialPublicacionRepository.findByPublicacionInmuebleId(id));

        publicacionInmuebleRepository.delete(publicacion);
    }

    public PublicacionInmuebleResponse actualizar(UUID id, ActualizarPublicacionInmuebleRequest request) {
        PublicacionInmueble publicacion = obtenerPublicacion(id);

        if (request.getTipoOperacion() != null) {
            publicacion.setTipoOperacion(TipoOperacionPublicacion.valueOf(request.getTipoOperacion()));
        }
        if (request.getMostrarPrecio() != null) {
            publicacion.setMostrarPrecio(request.getMostrarPrecio());
        }
        if (request.getMostrarContacto() != null) {
            publicacion.setMostrarContacto(request.getMostrarContacto());
        }
        if (request.getMostrarCorreo() != null) {
            publicacion.setMostrarCorreo(request.getMostrarCorreo());
        }
        if (request.getComisionCompartida() != null) {
            publicacion.setComisionCompartida(request.getComisionCompartida());
        }
        if (request.getDestacada() != null) {
            publicacion.setDestacada(request.getDestacada());
        }
        if (request.getDescripcionCorta() != null) {
            publicacion.setDescripcionCorta(request.getDescripcionCorta());
        }
        if (request.getDescripcionLarga() != null) {
            publicacion.setDescripcionLarga(request.getDescripcionLarga());
        }
        if (request.getDescripcionOptimizada() != null) {
            publicacion.setDescripcionOptimizada(request.getDescripcionOptimizada());
        }

        PublicacionInmueble actualizada = publicacionInmuebleRepository.save(publicacion);

        registrarHistorial(actualizada, "Datos de la publicacion actualizados");

        return mapearAResponse(actualizada);
    }

    public PublicacionInmuebleResponse cambiarEstado(UUID id, CambiarEstadoPublicacionRequest request) {
        PublicacionInmueble publicacion = obtenerPublicacion(id);

        EstadoPublicacion nuevoEstado = EstadoPublicacion.valueOf(request.getNuevoEstado());

        if (nuevoEstado == EstadoPublicacion.ACTIVA) {
            publicacion.setFechaPublicacion(LocalDate.now());
            publicacion.setAlertaSinContactosEnviada(false);
        }

        publicacion.setEstado(nuevoEstado);

        PublicacionInmueble actualizada = publicacionInmuebleRepository.save(publicacion);

        registrarHistorial(actualizada, "Estado cambiado a " + nuevoEstado.name());

        return mapearAResponse(actualizada);
    }

    private PublicacionInmueble obtenerPublicacion(UUID id) {
        return publicacionInmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Publicacion no encontrada con id: " + id));
    }

    private void registrarHistorial(PublicacionInmueble publicacion, String evento) {
        HistorialPublicacion historial = HistorialPublicacion.builder()
                .publicacionInmueble(publicacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialPublicacionRepository.save(historial);
    }

    private PublicacionInmuebleResponse mapearAResponse(PublicacionInmueble publicacion) {
        return PublicacionInmuebleResponse.builder()
                .id(publicacion.getId())
                .inmuebleId(publicacion.getInmueble().getId())
                .inmuebleCodigo(publicacion.getInmueble().getCodigo())
                .tipoOperacion(publicacion.getTipoOperacion() != null ? publicacion.getTipoOperacion().name() : null)
                .mostrarPrecio(publicacion.isMostrarPrecio())
                .mostrarContacto(publicacion.isMostrarContacto())
                .mostrarCorreo(publicacion.isMostrarCorreo())
                .comisionCompartida(publicacion.isComisionCompartida())
                .destacada(publicacion.isDestacada())
                .descripcionCorta(publicacion.getDescripcionCorta())
                .descripcionLarga(publicacion.getDescripcionLarga())
                .descripcionOptimizada(publicacion.getDescripcionOptimizada())
                .estado(publicacion.getEstado().name())
                .fechaPublicacion(publicacion.getFechaPublicacion())
                .createdAt(publicacion.getCreatedAt())
                .build();
    }
}
