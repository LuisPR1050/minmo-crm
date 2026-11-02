package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.ChecklistJuridicoRequest;
import com.felipeparodi.crmbackend.captacion.dto.ChecklistJuridicoResponse;
import com.felipeparodi.crmbackend.captacion.entity.ChecklistJuridico;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.ChecklistJuridicoRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ChecklistJuridicoService {

    private final ChecklistJuridicoRepository checklistJuridicoRepository;
    private final InmuebleRepository inmuebleRepository;

    public ChecklistJuridicoService(ChecklistJuridicoRepository checklistJuridicoRepository,
                                     InmuebleRepository inmuebleRepository) {
        this.checklistJuridicoRepository = checklistJuridicoRepository;
        this.inmuebleRepository = inmuebleRepository;
    }

    public ChecklistJuridicoResponse crear(ChecklistJuridicoRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        ChecklistJuridico checklist = checklistJuridicoRepository.findByInmuebleId(request.getInmuebleId())
                .orElseGet(() -> ChecklistJuridico.builder().inmueble(inmueble).build());

        checklist.setTieneHipoteca(request.isTieneHipoteca());
        checklist.setValorHipoteca(request.getValorHipoteca());
        checklist.setTieneEmbargo(request.isTieneEmbargo());
        checklist.setValorEmbargo(request.getValorEmbargo());
        checklist.setTienePatrimonioFamilia(request.isTienePatrimonioFamilia());
        checklist.setTieneViviendaFamiliar(request.isTieneViviendaFamiliar());

        ChecklistJuridico guardado = checklistJuridicoRepository.save(checklist);

        return mapearAResponse(guardado);
    }

    public List<ChecklistJuridicoResponse> listarTodos() {
        return checklistJuridicoRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ChecklistJuridicoResponse buscarPorId(UUID id) {
        ChecklistJuridico checklist = checklistJuridicoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Checklist juridico no encontrado con id: " + id));

        return mapearAResponse(checklist);
    }

    public ChecklistJuridicoResponse buscarPorInmueble(UUID inmuebleId) {
        ChecklistJuridico checklist = checklistJuridicoRepository.findByInmuebleId(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe checklist juridico para el inmueble: " + inmuebleId));

        return mapearAResponse(checklist);
    }

    private ChecklistJuridicoResponse mapearAResponse(ChecklistJuridico checklist) {
        return ChecklistJuridicoResponse.builder()
                .id(checklist.getId())
                .inmuebleId(checklist.getInmueble().getId())
                .inmuebleCodigo(checklist.getInmueble().getCodigo())
                .tieneHipoteca(checklist.isTieneHipoteca())
                .valorHipoteca(checklist.getValorHipoteca())
                .tieneEmbargo(checklist.isTieneEmbargo())
                .valorEmbargo(checklist.getValorEmbargo())
                .tienePatrimonioFamilia(checklist.isTienePatrimonioFamilia())
                .tieneViviendaFamiliar(checklist.isTieneViviendaFamiliar())
                .createdAt(checklist.getCreatedAt())
                .build();
    }
}