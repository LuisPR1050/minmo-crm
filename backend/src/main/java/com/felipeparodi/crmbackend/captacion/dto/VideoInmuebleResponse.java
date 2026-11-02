package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class VideoInmuebleResponse {

    private UUID id;
    private UUID inmuebleId;
    private String url;
    private String origen;
    private String tipoContenido;
    private LocalDateTime createdAt;
}
