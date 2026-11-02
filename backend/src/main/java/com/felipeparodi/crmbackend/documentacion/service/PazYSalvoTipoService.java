package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.PazYSalvoTipoResponse;
import com.felipeparodi.crmbackend.documentacion.repository.PazYSalvoTipoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PazYSalvoTipoService {

    private final PazYSalvoTipoRepository pazYSalvoTipoRepository;

    public PazYSalvoTipoService(PazYSalvoTipoRepository pazYSalvoTipoRepository) {
        this.pazYSalvoTipoRepository = pazYSalvoTipoRepository;
    }

    public List<PazYSalvoTipoResponse> listarTodas() {
        return pazYSalvoTipoRepository.findAll()
                .stream()
                .map(p -> PazYSalvoTipoResponse.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .build())
                .toList();
    }
}
