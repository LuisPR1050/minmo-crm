package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ParteNegociacionResponse {

    private UUID id;
    private UUID negociacionId;
    private String rol;
    private Short orden;
    private String tipoDocumento;
    private String numeroDocumento;
    private String estadoCivil;
    private String direccion;
    private String correoElectronico;
}