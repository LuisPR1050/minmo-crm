package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ResumenEstadisticasPublicacionResponse {

    private UUID publicacionInmuebleId;
    private int totalVistas;
    private int totalContactos;
}
