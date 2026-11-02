package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DocumentoOtrosiPromesaResponse {

    private UUID id;
    private String url;
    private String descripcion;
    private LocalDateTime createdAt;
}
