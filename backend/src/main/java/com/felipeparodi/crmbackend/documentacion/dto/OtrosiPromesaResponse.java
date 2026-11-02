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
public class OtrosiPromesaResponse {

    private UUID id;
    private String numeroOtrosi;
    private UUID promesaCompraventaId;
    private String numeroContratoPromesa;
    private UUID negociacionId;
    private String numeroNegocio;
    private LocalDate fechaElaboracion;
    private String motivo;
    private String otroMotivoTexto;
    private String justificacion;
    private BigDecimal nuevoPrecio;
    private LocalDate nuevaFechaEscritura;
    private LocalDate nuevaFechaEntrega;
    private String nuevaDistribucionGastos;
    private String nuevosBienesIncluidos;
    private String nuevasCondicionesEspeciales;
    private String observaciones;
    private boolean aplicado;
    private LocalDateTime fechaAplicacion;
    private String textoOtrosi;
    private LocalDateTime fechaGeneracionTexto;
    private String pdfUrl;
    private LocalDateTime createdAt;
}
