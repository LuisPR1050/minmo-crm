package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AccesoEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.dto.AsignarAccesosEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.entity.AccesoEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaAcceso;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.AccesoEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaAccesoRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActaEntregaAccesoService {

    private final ActaEntregaAccesoRepository actaEntregaAccesoRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final AccesoEntregaRepository accesoEntregaRepository;

    public ActaEntregaAccesoService(ActaEntregaAccesoRepository actaEntregaAccesoRepository,
                                     ActaEntregaRepository actaEntregaRepository,
                                     AccesoEntregaRepository accesoEntregaRepository) {
        this.actaEntregaAccesoRepository = actaEntregaAccesoRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.accesoEntregaRepository = accesoEntregaRepository;
    }

    public List<AccesoEntregaResponse> asignar(UUID actaEntregaId, AsignarAccesosEntregaRequest request) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        List<ActaEntregaAcceso> yaAsignados = actaEntregaAccesoRepository.findByActaEntregaId(actaEntregaId);

        for (UUID accesoId : request.getAccesoIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(aa -> aa.getAccesoEntrega().getId().equals(accesoId));

            if (yaAsignado) {
                continue;
            }

            AccesoEntrega acceso = accesoEntregaRepository.findById(accesoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Acceso de entrega no encontrado con id: " + accesoId));

            ActaEntregaAcceso nuevaAsignacion = ActaEntregaAcceso.builder()
                    .actaEntrega(acta)
                    .accesoEntrega(acceso)
                    .entregado(true)
                    .build();

            actaEntregaAccesoRepository.save(nuevaAsignacion);
        }

        return listarPorActa(actaEntregaId);
    }

    public List<AccesoEntregaResponse> listarPorActa(UUID actaEntregaId) {
        return actaEntregaAccesoRepository.findByActaEntregaId(actaEntregaId)
                .stream()
                .filter(ActaEntregaAcceso::isEntregado)
                .map(aa -> AccesoEntregaResponse.builder()
                        .id(aa.getAccesoEntrega().getId())
                        .nombre(aa.getAccesoEntrega().getNombre())
                        .build())
                .toList();
    }
}
