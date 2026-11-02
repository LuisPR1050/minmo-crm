package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AccesoEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.repository.AccesoEntregaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AccesoEntregaService {

    private final AccesoEntregaRepository accesoEntregaRepository;

    public AccesoEntregaService(AccesoEntregaRepository accesoEntregaRepository) {
        this.accesoEntregaRepository = accesoEntregaRepository;
    }

    public List<AccesoEntregaResponse> listarTodas() {
        return accesoEntregaRepository.findAll()
                .stream()
                .map(a -> AccesoEntregaResponse.builder()
                        .id(a.getId())
                        .nombre(a.getNombre())
                        .build())
                .toList();
    }
}
