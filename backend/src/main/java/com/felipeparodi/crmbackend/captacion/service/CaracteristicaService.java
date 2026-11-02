package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.CaracteristicaResponse;
import com.felipeparodi.crmbackend.captacion.entity.Caracteristica;
import com.felipeparodi.crmbackend.captacion.repository.CaracteristicaRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CaracteristicaService {

    private final CaracteristicaRepository caracteristicaRepository;

    public CaracteristicaService(CaracteristicaRepository caracteristicaRepository) {
        this.caracteristicaRepository = caracteristicaRepository;
    }

    public List<CaracteristicaResponse> listarTodas() {
        return caracteristicaRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<CaracteristicaResponse> listarPorCategoria(String categoria) {
        return caracteristicaRepository.findByCategoria(categoria)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private CaracteristicaResponse mapearAResponse(Caracteristica caracteristica) {
        return CaracteristicaResponse.builder()
                .id(caracteristica.getId())
                .nombre(caracteristica.getNombre())
                .categoria(caracteristica.getCategoria())
                .build();
    }
}