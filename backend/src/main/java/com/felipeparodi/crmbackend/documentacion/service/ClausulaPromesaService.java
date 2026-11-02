package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ClausulaPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.repository.ClausulaPromesaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ClausulaPromesaService {

    private final ClausulaPromesaRepository clausulaPromesaRepository;

    public ClausulaPromesaService(ClausulaPromesaRepository clausulaPromesaRepository) {
        this.clausulaPromesaRepository = clausulaPromesaRepository;
    }

    public List<ClausulaPromesaResponse> listarTodas() {
        return clausulaPromesaRepository.findAll()
                .stream()
                .map(c -> ClausulaPromesaResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .toList();
    }
}
