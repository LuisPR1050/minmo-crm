package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.PlanDePagoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.PlanDePagoResponse;
import com.felipeparodi.crmbackend.negociacion.entity.MetodoPago;
import com.felipeparodi.crmbackend.negociacion.entity.MomentoPago;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
import com.felipeparodi.crmbackend.negociacion.entity.TipoRecursoPago;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PlanDePagoService {

    private final PlanDePagoRepository planDePagoRepository;
    private final NegociacionRepository negociacionRepository;
    private final ReciboPagoRepository reciboPagoRepository;

    public PlanDePagoService(PlanDePagoRepository planDePagoRepository,
                              NegociacionRepository negociacionRepository,
                              ReciboPagoRepository reciboPagoRepository) {
        this.planDePagoRepository = planDePagoRepository;
        this.negociacionRepository = negociacionRepository;
        this.reciboPagoRepository = reciboPagoRepository;
    }

    public PlanDePagoResponse crear(PlanDePagoRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        PlanDePago plan = PlanDePago.builder()
                .negociacion(negociacion)
                .tipoRecurso(TipoRecursoPago.valueOf(request.getTipoRecurso()))
                .numeroCuota(request.getNumeroCuota())
                .porcentaje(request.getPorcentaje())
                .valor(request.getValor())
                .momentoPago(request.getMomentoPago() != null
                        ? MomentoPago.valueOf(request.getMomentoPago()) : null)
                .metodoPago(request.getMetodoPago() != null
                        ? MetodoPago.valueOf(request.getMetodoPago()) : null)
                .fechaEsperada(request.getFechaEsperada())
                .build();

        PlanDePago guardado = planDePagoRepository.save(plan);

        return mapearAResponse(guardado);
    }

    public List<PlanDePagoResponse> listarPorNegociacion(UUID negociacionId) {
        return planDePagoRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public PlanDePagoResponse marcarPagada(UUID id, boolean pagada) {
        PlanDePago plan = planDePagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plan de pago no encontrado con id: " + id));

        plan.setPagada(pagada);
        PlanDePago actualizado = planDePagoRepository.save(plan);

        return mapearAResponse(actualizado);
    }

    public PlanDePagoResponse actualizar(UUID id, PlanDePagoRequest request) {
        PlanDePago plan = planDePagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plan de pago no encontrado con id: " + id));

        plan.setTipoRecurso(TipoRecursoPago.valueOf(request.getTipoRecurso()));
        plan.setNumeroCuota(request.getNumeroCuota());
        plan.setPorcentaje(request.getPorcentaje());
        plan.setValor(request.getValor());
        plan.setMomentoPago(request.getMomentoPago() != null ? MomentoPago.valueOf(request.getMomentoPago()) : null);
        plan.setMetodoPago(request.getMetodoPago() != null ? MetodoPago.valueOf(request.getMetodoPago()) : null);
        plan.setFechaEsperada(request.getFechaEsperada());

        PlanDePago actualizado = planDePagoRepository.save(plan);

        return mapearAResponse(actualizado);
    }

    public void eliminar(UUID id) {
        PlanDePago plan = planDePagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Plan de pago no encontrado con id: " + id));

        if (reciboPagoRepository.existsByPlanDePagoId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar esta cuota porque ya tiene un recibo de pago registrado");
        }

        planDePagoRepository.delete(plan);
    }

    private PlanDePagoResponse mapearAResponse(PlanDePago plan) {
        return PlanDePagoResponse.builder()
                .id(plan.getId())
                .negociacionId(plan.getNegociacion().getId())
                .tipoRecurso(plan.getTipoRecurso().name())
                .numeroCuota(plan.getNumeroCuota())
                .porcentaje(plan.getPorcentaje())
                .valor(plan.getValor())
                .momentoPago(plan.getMomentoPago() != null ? plan.getMomentoPago().name() : null)
                .metodoPago(plan.getMetodoPago() != null ? plan.getMetodoPago().name() : null)
                .fechaEsperada(plan.getFechaEsperada())
                .pagada(plan.isPagada())
                .build();
    }
}