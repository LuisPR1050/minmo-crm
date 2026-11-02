package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class ResultadoVisitaRequest {

    private UUID visitaId;
    private Short numeroVisita;
    private String estado;
    private String nivelInteres;
    private String intencionCompra;
    private String proximaAccion;
    private Short seguimientoNumero;
    private String seguimientoUnidad;
    private String observacionesAsesor;
    private List<UUID> objecionIds;
}