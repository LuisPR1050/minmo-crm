package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.CambiarEstadoVisitaRequest;
import com.felipeparodi.crmbackend.captacion.dto.VisitaCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.VisitaCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.entity.EstadoVisitaCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.VisitaCaptacion;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.VisitaCaptacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VisitaCaptacionService {

    private final VisitaCaptacionRepository visitaCaptacionRepository;
    private final InmuebleRepository inmuebleRepository;

    public VisitaCaptacionService(VisitaCaptacionRepository visitaCaptacionRepository,
                                   InmuebleRepository inmuebleRepository) {
        this.visitaCaptacionRepository = visitaCaptacionRepository;
        this.inmuebleRepository = inmuebleRepository;
    }

    public VisitaCaptacionResponse crear(VisitaCaptacionRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        VisitaCaptacion visita = VisitaCaptacion.builder()
                .inmueble(inmueble)
                .asesorId(request.getAsesorId())
                .fechaVisita(request.getFechaVisita())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .observaciones(request.getObservaciones())
                .build();

        VisitaCaptacion guardada = visitaCaptacionRepository.save(visita);

        return mapearAResponse(guardada);
    }

    public List<VisitaCaptacionResponse> listarTodos() {
        return visitaCaptacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public VisitaCaptacionResponse buscarPorId(UUID id) {
        VisitaCaptacion visita = visitaCaptacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        return mapearAResponse(visita);
    }

    public List<VisitaCaptacionResponse> listarPorInmueble(UUID inmuebleId) {
        return visitaCaptacionRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public VisitaCaptacionResponse cambiarEstado(UUID id, CambiarEstadoVisitaRequest request) {
        VisitaCaptacion visita = visitaCaptacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        visita.setEstado(EstadoVisitaCaptacion.valueOf(request.getNuevoEstado()));

        VisitaCaptacion actualizada = visitaCaptacionRepository.save(visita);

        return mapearAResponse(actualizada);
    }

    public void eliminar(UUID id) {
        VisitaCaptacion visita = visitaCaptacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        visitaCaptacionRepository.delete(visita);
    }

    private VisitaCaptacionResponse mapearAResponse(VisitaCaptacion visita) {
        return VisitaCaptacionResponse.builder()
                .id(visita.getId())
                .inmuebleId(visita.getInmueble().getId())
                .inmuebleCodigo(visita.getInmueble().getCodigo())
                .asesorId(visita.getAsesorId())
                .fechaVisita(visita.getFechaVisita())
                .horaInicio(visita.getHoraInicio())
                .horaFin(visita.getHoraFin())
                .estado(visita.getEstado().name())
                .observaciones(visita.getObservaciones())
                .createdAt(visita.getCreatedAt())
                .build();
    }
}