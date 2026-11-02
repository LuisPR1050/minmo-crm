package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class BienAsociadoRequest {

    private UUID negociacionId;
    private String tipo;
    private String matriculaInmobiliaria;
    private String referenciaCatastral;
}