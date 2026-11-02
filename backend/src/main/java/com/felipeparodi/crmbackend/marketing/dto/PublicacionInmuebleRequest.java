package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class PublicacionInmuebleRequest {

    private UUID inmuebleId;
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
