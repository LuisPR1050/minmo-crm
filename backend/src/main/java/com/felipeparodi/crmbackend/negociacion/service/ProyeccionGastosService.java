package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.negociacion.dto.ProyeccionGastosResponse;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

@Service
public class ProyeccionGastosService {

    private static final BigDecimal PORCENTAJE_NOTARIAL = new BigDecimal("0.003");
    private static final BigDecimal PORCENTAJE_REGISTRO = new BigDecimal("0.01");
    private static final BigDecimal PORCENTAJE_RETENCION_FUENTE = new BigDecimal("0.01");

    private final NegociacionRepository negociacionRepository;

    public ProyeccionGastosService(NegociacionRepository negociacionRepository) {
        this.negociacionRepository = negociacionRepository;
    }

    public ProyeccionGastosResponse calcular(UUID negociacionId) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + negociacionId));

        BigDecimal valorVenta = negociacion.getPrecioFinalAcordado() != null
                ? negociacion.getPrecioFinalAcordado()
                : negociacion.getPrecioInicialPublicado();

        if (valorVenta == null) {
            throw new IllegalArgumentException(
                    "La negociacion no tiene un precio final ni inicial publicado para proyectar gastos");
        }

        BigDecimal gastosNotariales = valorVenta.multiply(PORCENTAJE_NOTARIAL).setScale(2, RoundingMode.HALF_UP);
        BigDecimal derechosRegistro = valorVenta.multiply(PORCENTAJE_REGISTRO).setScale(2, RoundingMode.HALF_UP);
        BigDecimal retencionFuente = valorVenta.multiply(PORCENTAJE_RETENCION_FUENTE).setScale(2, RoundingMode.HALF_UP);

        BigDecimal totalEstimado = gastosNotariales.add(derechosRegistro).add(retencionFuente);

        return ProyeccionGastosResponse.builder()
                .negociacionId(negociacionId)
                .valorVenta(valorVenta)
                .gastosNotariales(gastosNotariales)
                .derechosRegistro(derechosRegistro)
                .retencionFuente(retencionFuente)
                .totalEstimado(totalEstimado)
                .build();
    }
}