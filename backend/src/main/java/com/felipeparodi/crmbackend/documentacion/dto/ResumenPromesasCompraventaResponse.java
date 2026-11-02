package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResumenPromesasCompraventaResponse {

    private long total;
    private long vigentes;
    private long renovados;
    private long finalizados;
}
