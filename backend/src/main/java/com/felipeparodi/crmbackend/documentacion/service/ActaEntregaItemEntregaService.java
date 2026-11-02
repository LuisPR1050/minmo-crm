package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarItemsEntregadosRequest;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaItemEntrega;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaItemEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import com.felipeparodi.crmbackend.negociacion.entity.ItemEntrega;
import com.felipeparodi.crmbackend.negociacion.repository.ItemEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ActaEntregaItemEntregaService {

    private final ActaEntregaItemEntregaRepository actaEntregaItemEntregaRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final ItemEntregaRepository itemEntregaRepository;

    public ActaEntregaItemEntregaService(ActaEntregaItemEntregaRepository actaEntregaItemEntregaRepository,
                                          ActaEntregaRepository actaEntregaRepository,
                                          ItemEntregaRepository itemEntregaRepository) {
        this.actaEntregaItemEntregaRepository = actaEntregaItemEntregaRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.itemEntregaRepository = itemEntregaRepository;
    }

    public List<ItemEntregaResponse> asignar(UUID actaEntregaId, AsignarItemsEntregadosRequest request) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        List<ActaEntregaItemEntrega> yaAsignados = actaEntregaItemEntregaRepository.findByActaEntregaId(actaEntregaId);

        for (UUID itemId : request.getItemEntregaIds()) {
            boolean yaAsignado = yaAsignados.stream()
                    .anyMatch(ai -> ai.getItemEntrega().getId().equals(itemId));

            if (yaAsignado) {
                continue;
            }

            ItemEntrega item = itemEntregaRepository.findById(itemId)
                    .orElseThrow(() -> new RecursoNoEncontradoException("Item de entrega no encontrado con id: " + itemId));

            ActaEntregaItemEntrega nuevaAsignacion = ActaEntregaItemEntrega.builder()
                    .actaEntrega(acta)
                    .itemEntrega(item)
                    .entregado(true)
                    .build();

            actaEntregaItemEntregaRepository.save(nuevaAsignacion);
        }

        return listarPorActa(actaEntregaId);
    }

    public List<ItemEntregaResponse> listarPorActa(UUID actaEntregaId) {
        return actaEntregaItemEntregaRepository.findByActaEntregaId(actaEntregaId)
                .stream()
                .filter(ActaEntregaItemEntrega::isEntregado)
                .map(ai -> ItemEntregaResponse.builder()
                        .id(ai.getItemEntrega().getId())
                        .nombre(ai.getItemEntrega().getNombre())
                        .build())
                .toList();
    }
}
