package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AnalisisObjecionesInmuebleResponse {

    private UUID inmuebleId;
    private String resumen;
    private List<String> recomendaciones;
    private int totalVisitas;
    private int visitasAnalizadas;
}
