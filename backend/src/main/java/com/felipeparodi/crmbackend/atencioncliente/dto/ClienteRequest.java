package com.felipeparodi.crmbackend.atencioncliente.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ClienteRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    private String telefono;

    private String whatsapp;
    private String correoElectronico;

    @NotBlank(message = "El tipo de contacto es obligatorio")
    private String tipoContacto;

    private String subtipoContacto;
    private String numeroContacto;
    private UUID asesorId;
}