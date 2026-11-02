package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ArchivoAdjuntoRequest {

    private UUID inmuebleId;
    private String tipo;
    private String url;
    private boolean esPrincipal;
}