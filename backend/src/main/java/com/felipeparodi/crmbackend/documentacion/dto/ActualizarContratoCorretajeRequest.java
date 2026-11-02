package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ActualizarContratoCorretajeRequest {

    private String ciudad;
    private String tipoContrato;
    private BigDecimal precioAutorizadoVenta;
    private BigDecimal precioMinimoAutorizado;
    private BigDecimal porcentajeComision;
    private BigDecimal valorFijoComision;
    private BigDecimal sobreprecioComision;
    private String formaPagoComision;
    private LocalDate fechaVigenciaHasta;
    private String opcionFirma;
    private String textoContrato;
}
