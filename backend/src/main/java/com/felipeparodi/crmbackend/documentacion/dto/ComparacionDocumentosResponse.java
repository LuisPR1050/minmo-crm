package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder
@AllArgsConstructor
public class ComparacionDocumentosResponse {

    private String nombreDocumentoA;
    private String nombreDocumentoB;
    private String resumenGeneral;
    private String nivelCambio;
    private List<DiferenciaDocumentoResponse> diferencias;
    private LocalDateTime fechaComparacion;
}
