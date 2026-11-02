package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ProyeccionGastosResponse {

    private UUID negociacionId;
    private BigDecimal valorVenta;
    private BigDecimal gastosNotariales;
    private BigDecimal derechosRegistro;
    private BigDecimal retencionFuente;
    private BigDecimal totalEstimado;
}