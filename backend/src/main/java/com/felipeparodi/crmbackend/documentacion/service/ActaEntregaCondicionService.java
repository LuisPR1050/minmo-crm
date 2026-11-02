package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarCondicionesEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CondicionEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaCondicion;
import com.felipeparodi.crmbackend.documentacion.entity.CondicionEntrega;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaCondicionRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.CondicionEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActaEntregaCondicionService {

    private final ActaEntregaCondicionRepository actaEntregaCondicionRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final CondicionEntregaRepository condicionEntregaRepository;

    public ActaEntregaCondicionService(ActaEntregaCondicionRepository actaEntregaCondicionRepository,
                                        ActaEntregaRepository actaEntregaRepository,
                                        CondicionEntregaRepository condicionEntregaRepository) {
        this.actaEntregaCondicionRepository = actaEntregaCondicionRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.condicionEntregaRepository = condicionEntregaRepository;
    }

    public List<CondicionEntregaResponse> asignar(UUID actaEntregaId, AsignarCondicionesEntregaRequest request) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        List<ActaEntregaCondicion> yaAsignadas = actaEntregaCondicionRepository.findByActaEntregaId(actaEntregaId);

        for (UUID condicionId : request.getCondicionIds()) {
            boolean yaAsignada = yaAsignadas.stream()
                    .anyMatch(ac -> ac.getCondicionEntrega().getId().equals(condicionId));

            if (yaAsignada) {
                continue;
            }

            CondicionEntrega condicion = condicionEntregaRepository.findById(condicionId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Condicion de entrega no encontrada con id: " + condicionId));

            ActaEntregaCondicion nuevaAsignacion = ActaEntregaCondicion.builder()
                    .actaEntrega(acta)
                    .condicionEntrega(condicion)
                    .cumple(true)
                    .build();

            actaEntregaCondicionRepository.save(nuevaAsignacion);
        }

        return listarPorActa(actaEntregaId);
    }

    public List<CondicionEntregaResponse> listarPorActa(UUID actaEntregaId) {
        return actaEntregaCondicionRepository.findByActaEntregaId(actaEntregaId)
                .stream()
                .filter(ActaEntregaCondicion::isCumple)
                .map(ac -> CondicionEntregaResponse.builder()
                        .id(ac.getCondicionEntrega().getId())
                        .nombre(ac.getCondicionEntrega().getNombre())
                        .build())
                .toList();
    }
}
