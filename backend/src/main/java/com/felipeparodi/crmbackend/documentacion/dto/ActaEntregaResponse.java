package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ActaEntregaResponse {

    private UUID id;
    private String numeroActa;
    private UUID negociacionId;
    private String numeroNegocio;
    private UUID promesaCompraventaId;
    private LocalDate fechaElaboracion;
    private LocalDateTime fechaHoraEntrega;
    private String ciudad;

    private UUID inmuebleId;
    private String inmuebleCodigo;
    private String direccionExacta;
    private String inmuebleCiudad;
    private String matriculaInmobiliaria;
    private String referenciaCatastral;

    private String vendedorNombre;
    private String vendedorDocumento;
    private String vendedorTelefono;
    private String compradorNombre;
    private String compradorDocumento;
    private String compradorTelefono;

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

    private LocalDateTime createdAt;
}
