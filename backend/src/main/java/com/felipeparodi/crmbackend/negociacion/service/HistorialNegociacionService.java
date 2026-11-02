package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.HistorialNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistorialNegociacionService {

    private final HistorialNegociacionRepository historialNegociacionRepository;

    public HistorialNegociacionService(HistorialNegociacionRepository historialNegociacionRepository) {
        this.historialNegociacionRepository = historialNegociacionRepository;
    }

    public List<HistorialNegociacionResponse> listarPorNegociacion(UUID negociacionId) {
        return historialNegociacionRepository.findByNegociacionIdOrderByFechaDesc(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private HistorialNegociacionResponse mapearAResponse(HistorialNegociacion historial) {
        return HistorialNegociacionResponse.builder()
                .id(historial.getId())
                .negociacionId(historial.getNegociacion().getId())
                .usuarioId(historial.getUsuarioId())
                .evento(historial.getEvento())
                .fecha(historial.getFecha())
                .build();
    }
}
