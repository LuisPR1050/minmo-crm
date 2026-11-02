package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ParteNegociacionRequest {

    private UUID negociacionId;
    private String rol;
    private Short orden;
    private String tipoDocumento;
    private String numeroDocumento;
    private String estadoCivil;
    private String direccion;
    private String correoElectronico;
}