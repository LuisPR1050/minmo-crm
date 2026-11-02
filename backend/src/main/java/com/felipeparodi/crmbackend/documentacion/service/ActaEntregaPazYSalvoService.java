package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarPazYSalvosRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PazYSalvoTipoResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaPazYSalvo;
import com.felipeparodi.crmbackend.documentacion.entity.PazYSalvoTipo;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaPazYSalvoRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PazYSalvoTipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActaEntregaPazYSalvoService {

    private final ActaEntregaPazYSalvoRepository actaEntregaPazYSalvoRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final PazYSalvoTipoRepository pazYSalvoTipoRepository;

    public ActaEntregaPazYSalvoService(ActaEntregaPazYSalvoRepository actaEntregaPazYSalvoRepository,
                                        ActaEntregaRepository actaEntregaRepository,
                                        PazYSalvoTipoRepository pazYSalvoTipoRepository) {
        this.actaEntregaPazYSalvoRepository = actaEntregaPazYSalvoRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.pazYSalvoTipoRepository = pazYSalvoTipoRepository;
    }

    public List<PazYSalvoTipoResponse> asignar(UUID actaEntregaId, AsignarPazYSalvosRequest request) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        List<ActaEntregaPazYSalvo> yaAsignados = actaEntregaPazYSalvoRepository.findByActaEntregaId(actaEntregaId);

        for (UUID tipoId : request.getPazYSalvoTipoIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(ap -> ap.getPazYSalvoTipo().getId().equals(tipoId));

            if (yaAsignado) {
                continue;
            }

            PazYSalvoTipo tipo = pazYSalvoTipoRepository.findById(tipoId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Tipo de paz y salvo no encontrado con id: " + tipoId));

            ActaEntregaPazYSalvo nuevaAsignacion = ActaEntregaPazYSalvo.builder()
                    .actaEntrega(acta)
                    .pazYSalvoTipo(tipo)
                    .alDia(true)
                    .build();

            actaEntregaPazYSalvoRepository.save(nuevaAsignacion);
        }

        return listarPorActa(actaEntregaId);
    }

    public List<PazYSalvoTipoResponse> listarPorActa(UUID actaEntregaId) {
        return actaEntregaPazYSalvoRepository.findByActaEntregaId(actaEntregaId)
                .stream()
                .filter(ActaEntregaPazYSalvo::isAlDia)
                .map(ap -> PazYSalvoTipoResponse.builder()
                        .id(ap.getPazYSalvoTipo().getId())
                        .nombre(ap.getPazYSalvoTipo().getNombre())
                        .build())
                .toList();
    }
}
