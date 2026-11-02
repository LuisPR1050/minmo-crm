package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class SolicitudVideoMarketingRequest {

    private UUID inmuebleId;
    private String objetivoPublicacion;
    private String estiloContenido;
    private Double duracionMinimaMinutos;
    private Double duracionMaximaMinutos;
    private String llamadoAccion;
    private String logoUrl;
}
