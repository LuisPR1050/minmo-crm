package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.AsignarCaracteristicasRequest;
import com.felipeparodi.crmbackend.captacion.dto.CaracteristicaResponse;
import com.felipeparodi.crmbackend.captacion.entity.Caracteristica;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.InmuebleCaracteristica;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.CaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
public class InmuebleCaracteristicaService {

    private final InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    private final InmuebleRepository inmuebleRepository;
    private final CaracteristicaRepository caracteristicaRepository;

    public InmuebleCaracteristicaService(InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository,
                                          InmuebleRepository inmuebleRepository,
                                          CaracteristicaRepository caracteristicaRepository) {
        this.inmuebleCaracteristicaRepository = inmuebleCaracteristicaRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<CaracteristicaResponse> asignar(UUID inmuebleId, AsignarCaracteristicasRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        Set<UUID> yaAsignadas = new HashSet<>();
        inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId)
                .forEach(ic -> yaAsignadas.add(ic.getCaracteristica().getId()));

        for (UUID caracteristicaId : request.getCaracteristicaIds()) {
            if (!yaAsignadas.add(caracteristicaId)) {
                continue;
            }

            Caracteristica caracteristica = caracteristicaRepository.findById(caracteristicaId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Caracteristica no encontrada con id: " + caracteristicaId));

            InmuebleCaracteristica nuevaAsignacion = InmuebleCaracteristica.builder()
                    .inmueble(inmueble)
                    .caracteristica(caracteristica)
                    .build();

            inmuebleCaracteristicaRepository.save(nuevaAsignacion);
        }

        return listarPorInmueble(inmuebleId);
    }

    public List<CaracteristicaResponse> listarPorInmueble(UUID inmuebleId) {
        return inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId)
                .stream()
                .map(ic -> CaracteristicaResponse.builder()
                        .id(ic.getCaracteristica().getId())
                        .nombre(ic.getCaracteristica().getNombre())
                        .categoria(ic.getCaracteristica().getCategoria())
                        .build())
                .toList();
    }
}