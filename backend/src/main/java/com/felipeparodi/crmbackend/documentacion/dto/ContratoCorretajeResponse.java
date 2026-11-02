package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ContratoCorretajeResponse {

    private UUID id;
    private String numeroContrato;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private UUID propietarioId;
    private String propietarioNombre;
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
    private String estado;
    private String textoContrato;
    private LocalDateTime fechaGeneracionTexto;
    private String pdfUrl;
    private LocalDateTime createdAt;
}
