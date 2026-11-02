package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.RolParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParteNegociacionService {

    private final ParteNegociacionRepository parteNegociacionRepository;
    private final NegociacionRepository negociacionRepository;

    public ParteNegociacionService(ParteNegociacionRepository parteNegociacionRepository,
                                    NegociacionRepository negociacionRepository) {
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.negociacionRepository = negociacionRepository;
    }

    public ParteNegociacionResponse crear(ParteNegociacionRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        ParteNegociacion parte = ParteNegociacion.builder()
                .negociacion(negociacion)
                .rol(RolParteNegociacion.valueOf(request.getRol()))
                .orden(request.getOrden())
                .tipoDocumento(request.getTipoDocumento())
                .numeroDocumento(request.getNumeroDocumento())
                .estadoCivil(request.getEstadoCivil())
                .direccion(request.getDireccion())
                .correoElectronico(request.getCorreoElectronico())
                .build();

        ParteNegociacion guardada = parteNegociacionRepository.save(parte);

        return mapearAResponse(guardada);
    }

    public List<ParteNegociacionResponse> listarPorNegociacion(UUID negociacionId) {
        return parteNegociacionRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ParteNegociacionResponse actualizar(UUID id, ParteNegociacionRequest request) {
        ParteNegociacion parte = parteNegociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parte no encontrada con id: " + id));

        parte.setRol(RolParteNegociacion.valueOf(request.getRol()));
        parte.setOrden(request.getOrden());
        parte.setTipoDocumento(request.getTipoDocumento());
        parte.setNumeroDocumento(request.getNumeroDocumento());
        parte.setEstadoCivil(request.getEstadoCivil());
        parte.setDireccion(request.getDireccion());
        parte.setCorreoElectronico(request.getCorreoElectronico());

        ParteNegociacion actualizada = parteNegociacionRepository.save(parte);

        return mapearAResponse(actualizada);
    }

    public void eliminar(UUID id) {
        ParteNegociacion parte = parteNegociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Parte no encontrada con id: " + id));

        parteNegociacionRepository.delete(parte);
    }

    private ParteNegociacionResponse mapearAResponse(ParteNegociacion parte) {
        return ParteNegociacionResponse.builder()
                .id(parte.getId())
                .negociacionId(parte.getNegociacion().getId())
                .rol(parte.getRol().name())
                .orden(parte.getOrden())
                .tipoDocumento(parte.getTipoDocumento())
                .numeroDocumento(parte.getNumeroDocumento())
                .estadoCivil(parte.getEstadoCivil())
                .direccion(parte.getDireccion())
                .correoElectronico(parte.getCorreoElectronico())
                .build();
    }
}