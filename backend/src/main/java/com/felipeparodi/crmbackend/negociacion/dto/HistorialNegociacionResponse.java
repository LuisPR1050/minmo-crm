package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HistorialNegociacionResponse {

    private UUID id;
    private UUID negociacionId;
    private UUID usuarioId;
    private String evento;
    private LocalDateTime fecha;
}
