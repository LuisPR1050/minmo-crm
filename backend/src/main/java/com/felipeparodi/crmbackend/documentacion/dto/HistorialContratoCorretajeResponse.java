package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HistorialContratoCorretajeResponse {

    private UUID id;
    private UUID contratoCorretajeId;
    private UUID usuarioId;
    private String evento;
    private LocalDateTime fecha;
}
