package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ClausulaCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.repository.ClausulaCorretajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClausulaCorretajeService {

    private final ClausulaCorretajeRepository clausulaCorretajeRepository;

    public ClausulaCorretajeService(ClausulaCorretajeRepository clausulaCorretajeRepository) {
        this.clausulaCorretajeRepository = clausulaCorretajeRepository;
    }

    public List<ClausulaCorretajeResponse> listarTodas() {
        return clausulaCorretajeRepository.findAll()
                .stream()
                .map(c -> ClausulaCorretajeResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .toList();
    }
}
