package com.felipeparodi.crmbackend.captacion.dto;

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
public class VisitaCaptacionResponse {

    private UUID id;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private UUID asesorId;
    private LocalDate fechaVisita;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String estado;
    private String observaciones;
    private LocalDateTime createdAt;
}