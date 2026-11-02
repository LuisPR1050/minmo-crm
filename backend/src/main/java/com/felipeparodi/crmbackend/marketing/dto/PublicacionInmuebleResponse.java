package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PublicacionInmuebleResponse {

    private UUID id;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private String tipoOperacion;
    private boolean mostrarPrecio;
    private boolean mostrarContacto;
    private boolean mostrarCorreo;
    private boolean comisionCompartida;
    private boolean destacada;
    private String descripcionCorta;
    private String descripcionLarga;
    private String descripcionOptimizada;
    private String estado;
    private LocalDate fechaPublicacion;
    private LocalDateTime createdAt;
}
