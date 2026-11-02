package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class SolicitudVideoMarketingResponse {

    private UUID id;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private String objetivoPublicacion;
    private String estiloContenido;
    private Double duracionMinimaMinutos;
    private Double duracionMaximaMinutos;
    private String llamadoAccion;
    private String logoUrl;
    private String musicaFondoUrl;
    private String estado;
    private String videoGeneradoUrl;
    private String tituloGenerado;
    private String subtituloGenerado;
    private String descripcionGenerada;
    private String llamadoAccionTexto;
    private String hashtags;
    private LocalDateTime fechaGeneracionCopy;
    private LocalDateTime createdAt;
}
