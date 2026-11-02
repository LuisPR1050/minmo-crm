package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.CondicionEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.repository.CondicionEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CondicionEntregaService {

    private final CondicionEntregaRepository condicionEntregaRepository;

    public CondicionEntregaService(CondicionEntregaRepository condicionEntregaRepository) {
        this.condicionEntregaRepository = condicionEntregaRepository;
    }

    public List<CondicionEntregaResponse> listarTodas() {
        return condicionEntregaRepository.findAll()
                .stream()
                .map(c -> CondicionEntregaResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .toList();
    }
}
