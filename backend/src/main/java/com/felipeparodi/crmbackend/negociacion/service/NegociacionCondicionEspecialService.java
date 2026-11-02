package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.AsignarCondicionesEspecialesRequest;
import com.felipeparodi.crmbackend.negociacion.dto.CondicionEspecialResponse;
import com.felipeparodi.crmbackend.negociacion.entity.CondicionEspecial;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.NegociacionCondicionEspecial;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.CondicionEspecialRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionCondicionEspecialRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class NegociacionCondicionEspecialService {

    private final NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository;
    private final NegociacionRepository negociacionRepository;
    private final CondicionEspecialRepository condicionEspecialRepository;

    public NegociacionCondicionEspecialService(NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository,
                                                NegociacionRepository negociacionRepository,
                                                CondicionEspecialRepository condicionEspecialRepository) {
        this.negociacionCondicionEspecialRepository = negociacionCondicionEspecialRepository;
        this.negociacionRepository = negociacionRepository;
        this.condicionEspecialRepository = condicionEspecialRepository;
    }

    public List<CondicionEspecialResponse> asignar(UUID negociacionId, AsignarCondicionesEspecialesRequest request) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + negociacionId));

        Set<UUID> yaAsignadas = new HashSet<>();
        negociacionCondicionEspecialRepository.findByNegociacionId(negociacionId)
                .forEach(nce -> yaAsignadas.add(nce.getCondicionEspecial().getId()));

        for (UUID condicionId : request.getCondicionEspecialIds()) {
            if (!yaAsignadas.add(condicionId)) {
                continue;
            }

            CondicionEspecial condicion = condicionEspecialRepository.findById(condicionId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Condicion especial no encontrada con id: " + condicionId));

            NegociacionCondicionEspecial vinculo = NegociacionCondicionEspecial.builder()
                    .negociacion(negociacion)
                    .condicionEspecial(condicion)
                    .build();

            negociacionCondicionEspecialRepository.save(vinculo);
        }

        return listarPorNegociacion(negociacionId);
    }

    public List<CondicionEspecialResponse> listarPorNegociacion(UUID negociacionId) {
        return negociacionCondicionEspecialRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(nce -> CondicionEspecialResponse.builder()
                        .id(nce.getCondicionEspecial().getId())
                        .nombre(nce.getCondicionEspecial().getNombre())
                        .build())
                .toList();
    }
}