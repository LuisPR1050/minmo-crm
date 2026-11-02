package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.AsignarItemEntregaRequest;
import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import com.felipeparodi.crmbackend.negociacion.entity.ItemEntrega;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.NegociacionItemEntrega;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.ItemEntregaRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionItemEntregaRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NegociacionItemEntregaService {

    private final NegociacionItemEntregaRepository negociacionItemEntregaRepository;
    private final NegociacionRepository negociacionRepository;
    private final ItemEntregaRepository itemEntregaRepository;

    public NegociacionItemEntregaService(NegociacionItemEntregaRepository negociacionItemEntregaRepository,
                                          NegociacionRepository negociacionRepository,
                                          ItemEntregaRepository itemEntregaRepository) {
        this.negociacionItemEntregaRepository = negociacionItemEntregaRepository;
        this.negociacionRepository = negociacionRepository;
        this.itemEntregaRepository = itemEntregaRepository;
    }

    public void asignar(UUID negociacionId, AsignarItemEntregaRequest request) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + negociacionId));

        ItemEntrega item = itemEntregaRepository.findById(request.getItemEntregaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Item de entrega no encontrado con id: " + request.getItemEntregaId()));

        NegociacionItemEntrega vinculo = negociacionItemEntregaRepository
                .findByNegociacionIdAndItemEntregaId(negociacionId, request.getItemEntregaId())
                .orElseGet(() -> NegociacionItemEntrega.builder().negociacion(negociacion).itemEntrega(item).build());

        vinculo.setIncluido(request.isIncluido());

        negociacionItemEntregaRepository.save(vinculo);
    }

    public List<ItemEntregaResponse> listarIncluidosPorNegociacion(UUID negociacionId) {
        return negociacionItemEntregaRepository.findByNegociacionId(negociacionId)
                .stream()
                .filter(NegociacionItemEntrega::isIncluido)
                .map(nie -> ItemEntregaResponse.builder()
                        .id(nie.getItemEntrega().getId())
                        .nombre(nie.getItemEntrega().getNombre())
                        .build())
                .toList();
    }
}