package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CondicionCaptacionResponse {

    private UUID id;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private UUID asesorId;
    private String origenPropiedad;
    private String aliadoNombre;
    private String aliadoTelefono;
    private UUID aliadoId;
    private String whatsappAsesor;
    private String tipoComision;
    private BigDecimal valorComision;
    private boolean exclusividad;
    private LocalDateTime createdAt;
}