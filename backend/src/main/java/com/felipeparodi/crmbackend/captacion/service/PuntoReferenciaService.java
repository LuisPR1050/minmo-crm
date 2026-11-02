package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.PuntoReferenciaResponse;
import com.felipeparodi.crmbackend.captacion.entity.PuntoReferencia;
import com.felipeparodi.crmbackend.captacion.repository.PuntoReferenciaRepository;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;

@Service
public class PuntoReferenciaService {

    private final PuntoReferenciaRepository puntoReferenciaRepository;

    public PuntoReferenciaService(PuntoReferenciaRepository puntoReferenciaRepository) {
        this.puntoReferenciaRepository = puntoReferenciaRepository;
    }

    public List<PuntoReferenciaResponse> listarTodas() {
        return puntoReferenciaRepository.findAll()
                .stream()
                .sorted(Comparator.comparing(PuntoReferencia::getNombre, String.CASE_INSENSITIVE_ORDER))
                .map(pr -> PuntoReferenciaResponse.builder().id(pr.getId()).nombre(pr.getNombre()).build())
                .toList();
    }
}
