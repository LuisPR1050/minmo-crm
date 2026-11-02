package com.felipeparodi.crmbackend.documentacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ContratoCorretajeRequest {

    @NotNull(message = "Selecciona un inmueble")
    private UUID inmuebleId;

    @NotNull(message = "Selecciona un propietario")
    private UUID propietarioId;
    private LocalDate fechaElaboracion;
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
}
