package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class HistorialCaptacionRequest {

    private UUID inmuebleId;
    private UUID usuarioId;
    private String evento;
}
