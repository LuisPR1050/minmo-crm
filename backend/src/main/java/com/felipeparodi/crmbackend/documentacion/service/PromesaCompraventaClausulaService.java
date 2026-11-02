package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarClausulasPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ClausulaPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ClausulaPromesa;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventaClausula;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ClausulaPromesaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PromesaCompraventaClausulaService {

    private final PromesaCompraventaClausulaRepository promesaCompraventaClausulaRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final ClausulaPromesaRepository clausulaPromesaRepository;

    public PromesaCompraventaClausulaService(PromesaCompraventaClausulaRepository promesaCompraventaClausulaRepository,
                                              PromesaCompraventaRepository promesaCompraventaRepository,
                                              ClausulaPromesaRepository clausulaPromesaRepository) {
        this.promesaCompraventaClausulaRepository = promesaCompraventaClausulaRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.clausulaPromesaRepository = clausulaPromesaRepository;
    }

    public List<ClausulaPromesaResponse> asignar(UUID promesaCompraventaId, AsignarClausulasPromesaRequest request) {
        PromesaCompraventa promesa = promesaCompraventaRepository.findById(promesaCompraventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Promesa de compraventa no encontrada con id: " + promesaCompraventaId));

        List<PromesaCompraventaClausula> yaAsignadas = promesaCompraventaClausulaRepository
                .findByPromesaCompraventaId(promesaCompraventaId);

        for (UUID clausulaId : request.getClausulaIds()) {
            boolean yaAsignada = yaAsignadas.stream()
                    .anyMatch(pc -> pc.getClausulaPromesa().getId().equals(clausulaId));

            if (yaAsignada) {
                continue;
            }

            ClausulaPromesa clausula = clausulaPromesaRepository.findById(clausulaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Clausula de promesa no encontrada con id: " + clausulaId));

            PromesaCompraventaClausula nuevaAsignacion = PromesaCompraventaClausula.builder()
                    .promesaCompraventa(promesa)
                    .clausulaPromesa(clausula)
                    .incluida(true)
                    .build();

            promesaCompraventaClausulaRepository.save(nuevaAsignacion);
        }

        return listarPorPromesa(promesaCompraventaId);
    }

    public List<ClausulaPromesaResponse> listarPorPromesa(UUID promesaCompraventaId) {
        return promesaCompraventaClausulaRepository.findByPromesaCompraventaId(promesaCompraventaId)
                .stream()
                .filter(PromesaCompraventaClausula::isIncluida)
                .map(pc -> ClausulaPromesaResponse.builder()
                        .id(pc.getClausulaPromesa().getId())
                        .nombre(pc.getClausulaPromesa().getNombre())
                        .build())
                .toList();
    }
}
