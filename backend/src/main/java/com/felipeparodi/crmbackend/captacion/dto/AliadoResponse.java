package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class AliadoResponse {

    private UUID id;
    private String nombre;
    private String telefono;
    private String tipo;
    private LocalDateTime createdAt;
}
