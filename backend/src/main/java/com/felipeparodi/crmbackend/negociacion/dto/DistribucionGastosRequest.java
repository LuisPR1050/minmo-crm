package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class DistribucionGastosRequest {

    private UUID negociacionId;
    private String gastosEscrituracion;
    private String retencionFuente;
    private String gastosRegistro;
    private String gastosCancelacionGravamen;
    private String gastosConstitucionGravamen;
}