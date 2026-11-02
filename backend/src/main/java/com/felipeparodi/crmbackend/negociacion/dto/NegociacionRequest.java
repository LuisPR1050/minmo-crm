package com.felipeparodi.crmbackend.negociacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class NegociacionRequest {

    @NotNull(message = "Selecciona un inmueble")
    private UUID inmuebleId;

    @NotNull(message = "Selecciona un cliente")
    private UUID clienteId;

    @NotNull(message = "Selecciona un propietario")
    private UUID propietarioId;

    private UUID asesorId;
    private LocalDate fechaInicio;
    private BigDecimal precioInicialPublicado;
    private BigDecimal ofertaInicialComprador;
}