package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.EstadisticaPortalRequest;
import com.felipeparodi.crmbackend.marketing.dto.EstadisticaPortalResponse;
import com.felipeparodi.crmbackend.marketing.dto.ResumenEstadisticasPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.entity.EstadisticaPortal;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueblePortal;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.EstadisticaPortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class EstadisticaPortalService {

    private final EstadisticaPortalRepository estadisticaPortalRepository;
    private final PublicacionInmueblePortalRepository publicacionInmueblePortalRepository;

    public EstadisticaPortalService(EstadisticaPortalRepository estadisticaPortalRepository,
                                     PublicacionInmueblePortalRepository publicacionInmueblePortalRepository) {
        this.estadisticaPortalRepository = estadisticaPortalRepository;
        this.publicacionInmueblePortalRepository = publicacionInmueblePortalRepository;
    }

    public EstadisticaPortalResponse registrar(UUID publicacionInmueblePortalId, EstadisticaPortalRequest request) {
        PublicacionInmueblePortal vinculo = publicacionInmueblePortalRepository.findById(publicacionInmueblePortalId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Vinculo publicacion-portal no encontrado con id: " + publicacionInmueblePortalId));

        EstadisticaPortal estadistica = EstadisticaPortal.builder()
                .publicacionInmueblePortal(vinculo)
                .vistas(request.getVistas())
                .contactos(request.getContactos())
                .fechaCorte(request.getFechaCorte())
                .build();

        EstadisticaPortal guardada = estadisticaPortalRepository.save(estadistica);

        return mapearAResponse(guardada);
    }

    public List<EstadisticaPortalResponse> listarPorVinculo(UUID publicacionInmueblePortalId) {
        return estadisticaPortalRepository.findByPublicacionInmueblePortalId(publicacionInmueblePortalId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ResumenEstadisticasPublicacionResponse resumenPorPublicacion(UUID publicacionInmuebleId) {
        long totalVistas = estadisticaPortalRepository.sumVistasPorPublicacion(publicacionInmuebleId);
        long totalContactos = estadisticaPortalRepository.sumContactosPorPublicacion(publicacionInmuebleId);

        return ResumenEstadisticasPublicacionResponse.builder()
                .publicacionInmuebleId(publicacionInmuebleId)
                .totalVistas((int) totalVistas)
                .totalContactos((int) totalContactos)
                .build();
    }

    private EstadisticaPortalResponse mapearAResponse(EstadisticaPortal estadistica) {
        return EstadisticaPortalResponse.builder()
                .id(estadistica.getId())
                .vistas(estadistica.getVistas())
                .contactos(estadistica.getContactos())
                .fechaCorte(estadistica.getFechaCorte())
                .build();
    }
}
