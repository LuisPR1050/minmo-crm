package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class HistorialPublicacionResponse {

    private UUID id;
    private UUID publicacionInmuebleId;
    private String evento;
    private LocalDateTime fecha;
}
