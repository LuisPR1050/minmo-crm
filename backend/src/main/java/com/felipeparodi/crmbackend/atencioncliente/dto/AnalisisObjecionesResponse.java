package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AnalisisObjecionesResponse {

    private UUID clienteId;
    private String resumen;
    private List<String> recomendaciones;
    private int visitasAnalizadas;
}
