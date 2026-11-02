package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ClienteResponse {

    private UUID id;
    private String nombre;
    private String telefono;
    private String whatsapp;
    private String correoElectronico;
    private String tipoContacto;
    private String subtipoContacto;
    private String numeroContacto;
    private UUID asesorId;
    private String estadoPipeline;
    private LocalDateTime createdAt;
}