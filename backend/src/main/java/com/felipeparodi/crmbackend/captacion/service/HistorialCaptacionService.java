package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.HistorialCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.HistorialCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.entity.HistorialCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.HistorialCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class HistorialCaptacionService {

    private final HistorialCaptacionRepository historialCaptacionRepository;
    private final InmuebleRepository inmuebleRepository;

    public HistorialCaptacionService(HistorialCaptacionRepository historialCaptacionRepository,
                                      InmuebleRepository inmuebleRepository) {
        this.historialCaptacionRepository = historialCaptacionRepository;
        this.inmuebleRepository = inmuebleRepository;
    }

    public HistorialCaptacionResponse crear(HistorialCaptacionRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        HistorialCaptacion historial = HistorialCaptacion.builder()
                .inmueble(inmueble)
                .usuarioId(request.getUsuarioId())
                .evento(request.getEvento())
                .build();

        HistorialCaptacion guardado = historialCaptacionRepository.save(historial);

        return mapearAResponse(guardado);
    }

    public List<HistorialCaptacionResponse> listarTodos() {
        return historialCaptacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public HistorialCaptacionResponse buscarPorId(UUID id) {
        HistorialCaptacion historial = historialCaptacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Historial no encontrado con id: " + id));

        return mapearAResponse(historial);
    }

    public List<HistorialCaptacionResponse> listarPorInmueble(UUID inmuebleId) {
        return historialCaptacionRepository.findByInmuebleIdOrderByFechaDesc(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private HistorialCaptacionResponse mapearAResponse(HistorialCaptacion historial) {
        return HistorialCaptacionResponse.builder()
                .id(historial.getId())
                .inmuebleId(historial.getInmueble().getId())
                .usuarioId(historial.getUsuarioId())
                .evento(historial.getEvento())
                .fecha(historial.getFecha())
                .build();
    }
}