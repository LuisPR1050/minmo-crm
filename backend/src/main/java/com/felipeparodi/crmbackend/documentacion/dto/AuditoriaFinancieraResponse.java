package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AuditoriaFinancieraResponse {

    private UUID negociacionId;
    private String numeroNegocio;
    private String resumenGeneral;
    private boolean consistente;
    private List<HallazgoRevisionResponse> hallazgos;
    private LocalDateTime fechaAuditoria;
}
