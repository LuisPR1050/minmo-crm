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
public class RevisionActaEntregaResponse {

    private UUID actaEntregaId;
    private String numeroActa;
    private String resumenGeneral;
    private boolean listaParaFirmar;
    private List<HallazgoRevisionResponse> hallazgos;
    private LocalDateTime fechaRevision;
}
