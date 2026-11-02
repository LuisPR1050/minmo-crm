package com.felipeparodi.crmbackend.documentacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class OtrosiPromesaRequest {

    @NotNull(message = "Selecciona una promesa de compraventa")
    private UUID promesaCompraventaId;
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
}
