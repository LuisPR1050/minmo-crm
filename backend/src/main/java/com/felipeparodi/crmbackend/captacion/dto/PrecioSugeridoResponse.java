package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PrecioSugeridoResponse {

    private UUID inmuebleId;
    private Long precioSugerido;
    private Long rangoMinimo;
    private Long rangoMaximo;
    private String justificacion;
    private int comparablesUsados;
}
