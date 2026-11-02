package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DistribucionGastosResponse {

    private UUID id;
    private UUID negociacionId;
    private String gastosEscrituracion;
    private String retencionFuente;
    private String gastosRegistro;
    private String gastosCancelacionGravamen;
    private String gastosConstitucionGravamen;
}