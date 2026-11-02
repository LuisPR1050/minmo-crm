package com.felipeparodi.crmbackend.captacion.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PropietarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono principal es obligatorio")
    private String telefonoPrincipal;

    private String whatsapp;
    private String correoElectronico;

    @NotBlank(message = "El tipo de contacto es obligatorio")
    private String tipoContacto;

    private String tipoIncentivo;
    private BigDecimal valorIncentivo;
    private String referenciadorNombre;
    private String referenciadorTelefono;
}