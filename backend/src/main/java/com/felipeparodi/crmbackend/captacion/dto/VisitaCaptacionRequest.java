package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class VisitaCaptacionRequest {

    private UUID inmuebleId;
    private UUID asesorId;
    private LocalDate fechaVisita;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String observaciones;
}