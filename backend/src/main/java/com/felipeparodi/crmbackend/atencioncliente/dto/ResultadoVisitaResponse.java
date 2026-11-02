package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ResultadoVisitaResponse {

    private UUID id;
    private UUID visitaId;
    private Short numeroVisita;
    private String estado;
    private String nivelInteres;
    private String intencionCompra;
    private String proximaAccion;
    private Short seguimientoNumero;
    private String seguimientoUnidad;
    private String observacionesAsesor;
    private List<String> objeciones;
    private LocalDateTime createdAt;
    private List<InmuebleRecomendadoResponse> recomendacionesAlternativas;
    private String resumenGeneradoIA;
}