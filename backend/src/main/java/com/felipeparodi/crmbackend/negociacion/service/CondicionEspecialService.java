package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.CondicionEspecialResponse;
import com.felipeparodi.crmbackend.negociacion.repository.CondicionEspecialRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CondicionEspecialService {

    private final CondicionEspecialRepository condicionEspecialRepository;

    public CondicionEspecialService(CondicionEspecialRepository condicionEspecialRepository) {
        this.condicionEspecialRepository = condicionEspecialRepository;
    }

    public List<CondicionEspecialResponse> listarTodas() {
        return condicionEspecialRepository.findAll()
                .stream()
                .map(c -> CondicionEspecialResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .toList();
    }
}