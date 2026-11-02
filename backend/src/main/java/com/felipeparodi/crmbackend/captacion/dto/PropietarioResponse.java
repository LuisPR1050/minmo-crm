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
public class PropietarioResponse {

    private UUID id;
    private String nombre;
    private String telefonoPrincipal;
    private String whatsapp;
    private String correoElectronico;
    private String tipoContacto;
    private String tipoIncentivo;
    private BigDecimal valorIncentivo;
    private String referenciadorNombre;
    private String referenciadorTelefono;
    private LocalDateTime createdAt;
}