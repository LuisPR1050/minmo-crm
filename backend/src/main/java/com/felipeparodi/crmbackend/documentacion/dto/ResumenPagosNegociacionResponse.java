package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ResumenPagosNegociacionResponse {

    private UUID negociacionId;
    private BigDecimal totalPactado;
    private BigDecimal totalPagado;
    private BigDecimal saldoPendiente;
    private long cantidadRecibos;
}
