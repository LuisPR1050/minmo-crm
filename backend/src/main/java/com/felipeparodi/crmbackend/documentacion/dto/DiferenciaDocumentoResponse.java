package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class DiferenciaDocumentoResponse {

    private String seccion;
    private String contenidoDocumentoA;
    private String contenidoDocumentoB;
    private String tipoCambio;
    private String relevancia;
}
