package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.AsignarPortalesRequest;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoPublicacionPortalRequest;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmueblePortalResponse;
import com.felipeparodi.crmbackend.marketing.entity.EstadoPublicacionPortal;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PortalInmobiliario;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueblePortal;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PortalInmobiliarioRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class PublicacionInmueblePortalService {

    private final PublicacionInmueblePortalRepository publicacionInmueblePortalRepository;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final PortalInmobiliarioRepository portalInmobiliarioRepository;
    private final HistorialPublicacionRepository historialPublicacionRepository;

    public PublicacionInmueblePortalService(PublicacionInmueblePortalRepository publicacionInmueblePortalRepository,
                                             PublicacionInmuebleRepository publicacionInmuebleRepository,
                                             PortalInmobiliarioRepository portalInmobiliarioRepository,
                                             HistorialPublicacionRepository historialPublicacionRepository) {
        this.publicacionInmueblePortalRepository = publicacionInmueblePortalRepository;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.portalInmobiliarioRepository = portalInmobiliarioRepository;
        this.historialPublicacionRepository = historialPublicacionRepository;
    }

    public List<PublicacionInmueblePortalResponse> asignar(UUID publicacionInmuebleId, AsignarPortalesRequest request) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionInmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionInmuebleId));

        List<PublicacionInmueblePortal> yaAsignados = publicacionInmueblePortalRepository
                .findByPublicacionInmuebleId(publicacionInmuebleId);

        for (UUID portalId : request.getPortalIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(pp -> pp.getPortalInmobiliario().getId().equals(portalId));

            if (yaAsignado) {
                continue;
            }

            PortalInmobiliario portal = portalInmobiliarioRepository.findById(portalId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Portal no encontrado con id: " + portalId));

            PublicacionInmueblePortal nuevaAsignacion = PublicacionInmueblePortal.builder()
                    .publicacionInmueble(publicacion)
                    .portalInmobiliario(portal)
                    .estado(EstadoPublicacionPortal.ACTIVA)
                    .build();

            publicacionInmueblePortalRepository.save(nuevaAsignacion);

            registrarHistorial(publicacion, "Portal asignado: " + portal.getNombre());
        }

        return listarPorPublicacion(publicacionInmuebleId);
    }

    public List<PublicacionInmueblePortalResponse> listarPorPublicacion(UUID publicacionInmuebleId) {
        return publicacionInmueblePortalRepository.findByPublicacionInmuebleId(publicacionInmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public PublicacionInmueblePortalResponse cambiarEstado(UUID publicacionInmueblePortalId,
                                                             CambiarEstadoPublicacionPortalRequest request) {
        PublicacionInmueblePortal vinculo = publicacionInmueblePortalRepository.findById(publicacionInmueblePortalId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Vinculo publicacion-portal no encontrado con id: " + publicacionInmueblePortalId));

        EstadoPublicacionPortal nuevoEstado = EstadoPublicacionPortal.valueOf(request.getNuevoEstado());
        vinculo.setEstado(nuevoEstado);

        PublicacionInmueblePortal actualizado = publicacionInmueblePortalRepository.save(vinculo);

        registrarHistorial(vinculo.getPublicacionInmueble(),
                "Portal " + vinculo.getPortalInmobiliario().getNombre() + " cambio a " + nuevoEstado.name());

        return mapearAResponse(actualizado);
    }

    private void registrarHistorial(PublicacionInmueble publicacion, String evento) {
        HistorialPublicacion historial = HistorialPublicacion.builder()
                .publicacionInmueble(publicacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialPublicacionRepository.save(historial);
    }

    private PublicacionInmueblePortalResponse mapearAResponse(PublicacionInmueblePortal vinculo) {
        return PublicacionInmueblePortalResponse.builder()
                .id(vinculo.getId())
                .portalId(vinculo.getPortalInmobiliario().getId())
                .portalNombre(vinculo.getPortalInmobiliario().getNombre())
                .estado(vinculo.getEstado().name())
                .build();
    }
}
