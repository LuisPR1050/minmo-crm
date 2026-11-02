package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class VisitaClienteResponse {

    private UUID id;
    private UUID clienteId;
    private String clienteNombre;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private String codigoVisita;
    private String estado;
    private String tipoVisita;
    private LocalDate fechaVisita;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private boolean confirmadaPorCliente;
    private boolean confirmadaPorPropietario;
    private String recomendaciones;
    private LocalDateTime createdAt;
}