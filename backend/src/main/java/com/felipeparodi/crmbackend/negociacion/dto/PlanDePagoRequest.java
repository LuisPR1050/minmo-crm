package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PlanDePagoRequest {

    private UUID negociacionId;
    private String tipoRecurso;
    private Short numeroCuota;
    private BigDecimal porcentaje;
    private BigDecimal valor;
    private String momentoPago;
    private String metodoPago;
    private LocalDate fechaEsperada;
}