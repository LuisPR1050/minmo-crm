package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.ObjecionResponse;
import com.felipeparodi.crmbackend.atencioncliente.repository.ObjecionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ObjecionService {

    private final ObjecionRepository objecionRepository;

    public ObjecionService(ObjecionRepository objecionRepository) {
        this.objecionRepository = objecionRepository;
    }

    public List<ObjecionResponse> listarTodas() {
        return objecionRepository.findAll()
                .stream()
                .map(o -> ObjecionResponse.builder()
                        .id(o.getId())
                        .nombre(o.getNombre())
                        .build())
                .toList();
    }
}