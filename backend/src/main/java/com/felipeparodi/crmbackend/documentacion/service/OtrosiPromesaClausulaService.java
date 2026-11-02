package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasOtrosiRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ClausulaCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesa;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesaClausula;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ClausulaCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class OtrosiPromesaClausulaService {

    private final OtrosiPromesaClausulaRepository otrosiPromesaClausulaRepository;
    private final OtrosiPromesaRepository otrosiPromesaRepository;
    private final ClausulaCorretajeRepository clausulaCorretajeRepository;

    public OtrosiPromesaClausulaService(OtrosiPromesaClausulaRepository otrosiPromesaClausulaRepository,
                                         OtrosiPromesaRepository otrosiPromesaRepository,
                                         ClausulaCorretajeRepository clausulaCorretajeRepository) {
        this.otrosiPromesaClausulaRepository = otrosiPromesaClausulaRepository;
        this.otrosiPromesaRepository = otrosiPromesaRepository;
        this.clausulaCorretajeRepository = clausulaCorretajeRepository;
    }

    public List<ClausulaCorretajeResponse> asignar(UUID otrosiPromesaId, AsignarClausulasOtrosiRequest request) {
        OtrosiPromesa otrosi = otrosiPromesaRepository.findById(otrosiPromesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Otrosi no encontrado con id: " + otrosiPromesaId));

        List<OtrosiPromesaClausula> yaAsignadas = otrosiPromesaClausulaRepository.findByOtrosiPromesaId(otrosiPromesaId);

        for (UUID clausulaId : request.getClausulaIds()) {
            boolean yaAsignada = yaAsignadas.stream()
                    .anyMatch(oc -> oc.getClausulaCorretaje().getId().equals(clausulaId));

            if (yaAsignada) {
                continue;
            }

            ClausulaCorretaje clausula = clausulaCorretajeRepository.findById(clausulaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Clausula no encontrada con id: " + clausulaId));

            OtrosiPromesaClausula nuevaAsignacion = OtrosiPromesaClausula.builder()
                    .otrosiPromesa(otrosi)
                    .clausulaCorretaje(clausula)
                    .incluida(true)
                    .build();

            otrosiPromesaClausulaRepository.save(nuevaAsignacion);
        }

        return listarPorOtrosi(otrosiPromesaId);
    }

    public List<ClausulaCorretajeResponse> listarPorOtrosi(UUID otrosiPromesaId) {
        return otrosiPromesaClausulaRepository.findByOtrosiPromesaId(otrosiPromesaId)
                .stream()
                .filter(OtrosiPromesaClausula::isIncluida)
                .map(oc -> ClausulaCorretajeResponse.builder()
                        .id(oc.getClausulaCorretaje().getId())
                        .nombre(oc.getClausulaCorretaje().getNombre())
                        .build())
                .toList();
    }
}
