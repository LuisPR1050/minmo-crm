package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AnalisisFotosResponse {

    private UUID publicacionId;
    private Integer puntuacionGeneral;
    private List<String> fortalezas;
    private List<String> problemas;
    private List<String> recomendaciones;
    private List<String> fotosFaltantes;
    private int fotosAnalizadas;
}
