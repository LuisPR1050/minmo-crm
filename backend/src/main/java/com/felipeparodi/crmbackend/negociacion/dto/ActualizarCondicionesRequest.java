package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ActualizarCondicionesRequest {

    private BigDecimal contraofertaVendedor;
    private BigDecimal precioFinalAcordado;
    private LocalDate fechaFirmaPcv;
    private LocalDate fechaEntregaProgramada;
    private String entidadFinanciera;
    private String financiacionDirectaCondiciones;
    private String permutaCondiciones;
    private String estadoFisicoEntrega;
    private String momentoEntrega;
}