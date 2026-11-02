package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarLandingInmuebleRequest {

    private String descripcionPublica;
    private String ganchoEmocional;
    private Boolean landingActiva;
    private Boolean landingMostrarPrecio;
}
