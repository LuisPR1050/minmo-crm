package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.AliadoRequest;
import com.felipeparodi.crmbackend.captacion.dto.AliadoResponse;
import com.felipeparodi.crmbackend.captacion.entity.Aliado;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.AliadoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AliadoService {

    private final AliadoRepository aliadoRepository;

    public AliadoService(AliadoRepository aliadoRepository) {
        this.aliadoRepository = aliadoRepository;
    }

    public AliadoResponse crear(AliadoRequest request) {
        if (request.getNombre() == null || request.getNombre().isBlank()) {
            throw new IllegalArgumentException("El nombre del aliado es obligatorio");
        }

        Aliado aliado = Aliado.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .tipo(request.getTipo())
                .build();

        return mapearAResponse(aliadoRepository.save(aliado));
    }

    public List<AliadoResponse> listarTodos() {
        return aliadoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public AliadoResponse buscarPorId(UUID id) {
        Aliado aliado = aliadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Aliado no encontrado con id: " + id));

        return mapearAResponse(aliado);
    }

    private AliadoResponse mapearAResponse(Aliado aliado) {
        return AliadoResponse.builder()
                .id(aliado.getId())
                .nombre(aliado.getNombre())
                .telefono(aliado.getTelefono())
                .tipo(aliado.getTipo())
                .createdAt(aliado.getCreatedAt())
                .build();
    }
}
