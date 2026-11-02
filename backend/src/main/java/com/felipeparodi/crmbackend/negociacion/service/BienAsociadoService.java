package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.BienAsociadoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.BienAsociadoResponse;
import com.felipeparodi.crmbackend.negociacion.entity.BienAsociado;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.TipoBienAsociado;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.BienAsociadoRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class BienAsociadoService {

    private final BienAsociadoRepository bienAsociadoRepository;
    private final NegociacionRepository negociacionRepository;

    public BienAsociadoService(BienAsociadoRepository bienAsociadoRepository,
                                NegociacionRepository negociacionRepository) {
        this.bienAsociadoRepository = bienAsociadoRepository;
        this.negociacionRepository = negociacionRepository;
    }

    public BienAsociadoResponse crear(BienAsociadoRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        BienAsociado bien = BienAsociado.builder()
                .negociacion(negociacion)
                .tipo(TipoBienAsociado.valueOf(request.getTipo()))
                .matriculaInmobiliaria(request.getMatriculaInmobiliaria())
                .referenciaCatastral(request.getReferenciaCatastral())
                .build();

        BienAsociado guardado = bienAsociadoRepository.save(bien);

        return mapearAResponse(guardado);
    }

    public List<BienAsociadoResponse> listarPorNegociacion(UUID negociacionId) {
        return bienAsociadoRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public BienAsociadoResponse actualizar(UUID id, BienAsociadoRequest request) {
        BienAsociado bien = bienAsociadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Bien no encontrado con id: " + id));

        bien.setTipo(TipoBienAsociado.valueOf(request.getTipo()));
        bien.setMatriculaInmobiliaria(request.getMatriculaInmobiliaria());
        bien.setReferenciaCatastral(request.getReferenciaCatastral());

        BienAsociado actualizado = bienAsociadoRepository.save(bien);

        return mapearAResponse(actualizado);
    }

    public void eliminar(UUID id) {
        BienAsociado bien = bienAsociadoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Bien no encontrado con id: " + id));

        bienAsociadoRepository.delete(bien);
    }

    private BienAsociadoResponse mapearAResponse(BienAsociado bien) {
        return BienAsociadoResponse.builder()
                .id(bien.getId())
                .negociacionId(bien.getNegociacion().getId())
                .tipo(bien.getTipo().name())
                .matriculaInmobiliaria(bien.getMatriculaInmobiliaria())
                .referenciaCatastral(bien.getReferenciaCatastral())
                .build();
    }
}