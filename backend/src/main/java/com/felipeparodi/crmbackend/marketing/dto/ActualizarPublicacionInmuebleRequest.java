package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPublicacionInmuebleRequest {

    private String tipoOperacion;
    private Boolean mostrarPrecio;
    private Boolean mostrarContacto;
    private Boolean mostrarCorreo;
    private Boolean comisionCompartida;
    private Boolean destacada;
    private String descripcionCorta;
    private String descripcionLarga;
    private String descripcionOptimizada;
}
