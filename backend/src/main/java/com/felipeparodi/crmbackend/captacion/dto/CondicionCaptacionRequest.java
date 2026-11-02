package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class CondicionCaptacionRequest {

    private UUID inmuebleId;
    private UUID asesorId;
    private UUID propietarioId;
    private String origenPropiedad;
    private String aliadoNombre;
    private String aliadoTelefono;
    private UUID aliadoId;
    private String whatsappAsesor;
    private String tipoComision;
    private BigDecimal valorComision;
    private boolean exclusividad;
}