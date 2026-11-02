package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarSolicitudVideoMarketingRequest {

    private String objetivoPublicacion;
    private String estiloContenido;
    private Double duracionMinimaMinutos;
    private Double duracionMaximaMinutos;
    private String llamadoAccion;
    private String logoUrl;
}
