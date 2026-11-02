package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class ResumenContratosCorretajeResponse {

    private long total;
    private long vigentes;
    private long proximosAVencer;
    private long renovados;
    private long finalizados;
}
