package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.UUID;

@Getter
@Setter
public class VisitaClienteRequest {

    private UUID clienteId;
    private UUID inmuebleId;
    private String tipoVisita;
    private LocalDate fechaVisita;
    private LocalTime horaInicio;
    private LocalTime horaFin;
    private String recomendaciones;
}