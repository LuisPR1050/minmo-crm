package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.HistorialPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistorialPublicacionService {

    private final HistorialPublicacionRepository historialPublicacionRepository;

    public HistorialPublicacionService(HistorialPublicacionRepository historialPublicacionRepository) {
        this.historialPublicacionRepository = historialPublicacionRepository;
    }

    public List<HistorialPublicacionResponse> listarPorPublicacion(UUID publicacionInmuebleId) {
        return historialPublicacionRepository.findByPublicacionInmuebleIdOrderByFechaDesc(publicacionInmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private HistorialPublicacionResponse mapearAResponse(HistorialPublicacion historial) {
        return HistorialPublicacionResponse.builder()
                .id(historial.getId())
                .publicacionInmuebleId(historial.getPublicacionInmueble().getId())
                .evento(historial.getEvento())
                .fecha(historial.getFecha())
                .build();
    }
}
