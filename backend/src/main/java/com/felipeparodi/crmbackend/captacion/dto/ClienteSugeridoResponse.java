package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ClienteSugeridoResponse {

    private UUID clienteId;
    private String clienteNombre;
    private UUID requerimientoId;
    private String motivo;
}
