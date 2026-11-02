package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ArchivoAdjuntoResponse {

    private UUID id;
    private UUID inmuebleId;
    private String tipo;
    private String url;
    private boolean esPrincipal;
    private LocalDateTime createdAt;
}