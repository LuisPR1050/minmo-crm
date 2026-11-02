package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ClausulaCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeClausula;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ClausulaCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContratoCorretajeClausulaService {

    private final ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository;
    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final ClausulaCorretajeRepository clausulaCorretajeRepository;

    public ContratoCorretajeClausulaService(ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository,
                                             ContratoCorretajeRepository contratoCorretajeRepository,
                                             ClausulaCorretajeRepository clausulaCorretajeRepository) {
        this.contratoCorretajeClausulaRepository = contratoCorretajeClausulaRepository;
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.clausulaCorretajeRepository = clausulaCorretajeRepository;
    }

    public List<ClausulaCorretajeResponse> asignar(UUID contratoCorretajeId, AsignarClausulasCorretajeRequest request) {
        ContratoCorretaje contrato = contratoCorretajeRepository.findById(contratoCorretajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato de corretaje no encontrado con id: " + contratoCorretajeId));

        List<ContratoCorretajeClausula> yaAsignadas = contratoCorretajeClausulaRepository
                .findByContratoCorretajeId(contratoCorretajeId);

        for (UUID clausulaId : request.getClausulaIds()) {
            boolean yaAsignada = yaAsignadas.stream()
                    .anyMatch(cc -> cc.getClausulaCorretaje().getId().equals(clausulaId));

            if (yaAsignada) {
                continue;
            }

            ClausulaCorretaje clausula = clausulaCorretajeRepository.findById(clausulaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Clausula de corretaje no encontrada con id: " + clausulaId));

            ContratoCorretajeClausula nuevaAsignacion = ContratoCorretajeClausula.builder()
                    .contratoCorretaje(contrato)
                    .clausulaCorretaje(clausula)
                    .incluida(true)
                    .build();

            contratoCorretajeClausulaRepository.save(nuevaAsignacion);
        }

        return listarPorContrato(contratoCorretajeId);
    }

    public List<ClausulaCorretajeResponse> listarPorContrato(UUID contratoCorretajeId) {
        return contratoCorretajeClausulaRepository.findByContratoCorretajeId(contratoCorretajeId)
                .stream()
                .filter(ContratoCorretajeClausula::isIncluida)
                .map(cc -> ClausulaCorretajeResponse.builder()
                        .id(cc.getClausulaCorretaje().getId())
                        .nombre(cc.getClausulaCorretaje().getNombre())
                        .build())
                .toList();
    }
}
