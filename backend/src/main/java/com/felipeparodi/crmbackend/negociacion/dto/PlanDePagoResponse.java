package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PlanDePagoResponse {

    private UUID id;
    private UUID negociacionId;
    private String tipoRecurso;
    private Short numeroCuota;
    private BigDecimal porcentaje;
    private BigDecimal valor;
    private String momentoPago;
    private String metodoPago;
    private LocalDate fechaEsperada;
    private boolean pagada;
}