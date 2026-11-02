package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class ChecklistJuridicoRequest {

    private UUID inmuebleId;
    private boolean tieneHipoteca;
    private BigDecimal valorHipoteca;
    private boolean tieneEmbargo;
    private BigDecimal valorEmbargo;
    private boolean tienePatrimonioFamilia;
    private boolean tieneViviendaFamiliar;
}