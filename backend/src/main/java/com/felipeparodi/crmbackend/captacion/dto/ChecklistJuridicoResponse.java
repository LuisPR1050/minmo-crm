package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ChecklistJuridicoResponse {

    private UUID id;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private boolean tieneHipoteca;
    private BigDecimal valorHipoteca;
    private boolean tieneEmbargo;
    private BigDecimal valorEmbargo;
    private boolean tienePatrimonioFamilia;
    private boolean tieneViviendaFamiliar;
    private LocalDateTime createdAt;
}