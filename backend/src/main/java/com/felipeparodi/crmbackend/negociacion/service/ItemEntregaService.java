package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.ItemEntregaResponse;
import com.felipeparodi.crmbackend.negociacion.repository.ItemEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemEntregaService {

    private final ItemEntregaRepository itemEntregaRepository;

    public ItemEntregaService(ItemEntregaRepository itemEntregaRepository) {
        this.itemEntregaRepository = itemEntregaRepository;
    }

    public List<ItemEntregaResponse> listarTodos() {
        return itemEntregaRepository.findAll()
                .stream()
                .map(i -> ItemEntregaResponse.builder()
                        .id(i.getId())
                        .nombre(i.getNombre())
                        .build())
                .toList();
    }
}