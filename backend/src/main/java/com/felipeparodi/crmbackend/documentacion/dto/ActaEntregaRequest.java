package com.felipeparodi.crmbackend.documentacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class ActaEntregaRequest {

    @NotNull(message = "Selecciona una negociacion")
    private UUID negociacionId;
    private UUID promesaCompraventaId;
    private LocalDate fechaElaboracion;
    private LocalDateTime fechaHoraEntrega;
    private String ciudad;
    private String lecturaEnergia;
    private String lecturaAgua;
    private String lecturaGas;
    private String danosExistentes;
    private String reparacionesPendientes;
    private String acuerdosPosteriores;
    private String compromisosAsumidos;
    private String declaracionComprador;
    private boolean vendedorEntregaConforme;
    private String observaciones;
}
