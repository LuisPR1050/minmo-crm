package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class RequerimientoClienteResponse {

    private UUID id;
    private UUID clienteId;
    private String tipoPropiedad;
    private String ciudad;
    private String zona;
    private String entorno;
    private String sitiosClavesCercanos;
    private BigDecimal presupuestoMin;
    private BigDecimal presupuestoMax;
    private String formaPago;
    private String tiempoParaComprar;
    private Short numeroHabitaciones;
    private Short numeroBanos;
    private String observaciones;
    private LocalDateTime createdAt;
}