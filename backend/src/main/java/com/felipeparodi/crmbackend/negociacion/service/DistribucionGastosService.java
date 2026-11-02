package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.DistribucionGastosRequest;
import com.felipeparodi.crmbackend.negociacion.dto.DistribucionGastosResponse;
import com.felipeparodi.crmbackend.negociacion.entity.DistribucionGastos;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.QuienPaga;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.DistribucionGastosRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class DistribucionGastosService {

    private final DistribucionGastosRepository distribucionGastosRepository;
    private final NegociacionRepository negociacionRepository;

    public DistribucionGastosService(DistribucionGastosRepository distribucionGastosRepository,
                                      NegociacionRepository negociacionRepository) {
        this.distribucionGastosRepository = distribucionGastosRepository;
        this.negociacionRepository = negociacionRepository;
    }

    public DistribucionGastosResponse crear(DistribucionGastosRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        DistribucionGastos distribucion = distribucionGastosRepository.findByNegociacionId(request.getNegociacionId())
                .orElseGet(() -> DistribucionGastos.builder().negociacion(negociacion).build());

        distribucion.setGastosEscrituracion(QuienPaga.valueOf(request.getGastosEscrituracion()));
        distribucion.setRetencionFuente(QuienPaga.valueOf(request.getRetencionFuente()));
        distribucion.setGastosRegistro(QuienPaga.valueOf(request.getGastosRegistro()));
        distribucion.setGastosCancelacionGravamen(QuienPaga.valueOf(request.getGastosCancelacionGravamen()));
        distribucion.setGastosConstitucionGravamen(QuienPaga.valueOf(request.getGastosConstitucionGravamen()));

        DistribucionGastos guardada = distribucionGastosRepository.save(distribucion);

        return mapearAResponse(guardada);
    }

    public DistribucionGastosResponse buscarPorNegociacion(UUID negociacionId) {
        DistribucionGastos distribucion = distribucionGastosRepository.findByNegociacionId(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe distribucion de gastos para la negociacion: " + negociacionId));

        return mapearAResponse(distribucion);
    }

    private DistribucionGastosResponse mapearAResponse(DistribucionGastos distribucion) {
        return DistribucionGastosResponse.builder()
                .id(distribucion.getId())
                .negociacionId(distribucion.getNegociacion().getId())
                .gastosEscrituracion(distribucion.getGastosEscrituracion().name())
                .retencionFuente(distribucion.getRetencionFuente().name())
                .gastosRegistro(distribucion.getGastosRegistro().name())
                .gastosCancelacionGravamen(distribucion.getGastosCancelacionGravamen().name())
                .gastosConstitucionGravamen(distribucion.getGastosConstitucionGravamen().name())
                .build();
    }
}