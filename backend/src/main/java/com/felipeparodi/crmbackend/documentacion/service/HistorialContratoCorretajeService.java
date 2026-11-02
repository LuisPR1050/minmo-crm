package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.HistorialContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.HistorialContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.repository.HistorialContratoCorretajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class HistorialContratoCorretajeService {

    private final HistorialContratoCorretajeRepository historialContratoCorretajeRepository;

    public HistorialContratoCorretajeService(HistorialContratoCorretajeRepository historialContratoCorretajeRepository) {
        this.historialContratoCorretajeRepository = historialContratoCorretajeRepository;
    }

    public List<HistorialContratoCorretajeResponse> listarPorContrato(UUID contratoCorretajeId) {
        return historialContratoCorretajeRepository.findByContratoCorretajeIdOrderByFechaDesc(contratoCorretajeId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private HistorialContratoCorretajeResponse mapearAResponse(HistorialContratoCorretaje historial) {
        return HistorialContratoCorretajeResponse.builder()
                .id(historial.getId())
                .contratoCorretajeId(historial.getContratoCorretaje().getId())
                .usuarioId(historial.getUsuarioId())
                .evento(historial.getEvento())
                .fecha(historial.getFecha())
                .build();
    }
}
