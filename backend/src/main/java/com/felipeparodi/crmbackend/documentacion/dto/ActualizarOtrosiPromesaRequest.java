package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
public class ActualizarOtrosiPromesaRequest {

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
    private String textoOtrosi;
}
