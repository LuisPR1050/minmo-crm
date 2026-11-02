package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HistorialCaptacionResponse {

    private UUID id;
    private UUID inmuebleId;
    private UUID usuarioId;
    private String evento;
    private LocalDateTime fecha;
}