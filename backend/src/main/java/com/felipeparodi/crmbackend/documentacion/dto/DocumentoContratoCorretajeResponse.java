package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DocumentoContratoCorretajeResponse {

    private UUID id;
    private String tipo;
    private String url;
    private LocalDateTime createdAt;
}
