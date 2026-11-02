package com.felipeparodi.crmbackend.negociacion.dto;

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
public class NegociacionResponse {

    private UUID id;
    private String numeroNegocio;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private UUID clienteId;
    private String clienteNombre;
    private UUID propietarioId;
    private String propietarioNombre;
    private UUID asesorId;
    private LocalDate fechaInicio;
    private LocalDate fechaFirmaPcv;
    private LocalDate fechaEntregaProgramada;
    private BigDecimal precioInicialPublicado;
    private BigDecimal ofertaInicialComprador;
    private BigDecimal contraofertaVendedor;
    private BigDecimal precioFinalAcordado;
    private String entidadFinanciera;
    private String financiacionDirectaCondiciones;
    private String permutaCondiciones;
    private String estadoFisicoEntrega;
    private String momentoEntrega;
    private boolean escrituraFirmada;
    private boolean pagoRecibido;
    private boolean entregaRealizada;
    private String estado;
    private LocalDateTime createdAt;
}