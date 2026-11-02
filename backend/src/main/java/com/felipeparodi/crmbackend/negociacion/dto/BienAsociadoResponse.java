package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class BienAsociadoResponse {

    private UUID id;
    private UUID negociacionId;
    private String tipo;
    private String matriculaInmobiliaria;
    private String referenciaCatastral;
}