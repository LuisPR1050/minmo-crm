package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DocumentoNegociacionResponse {

    private UUID id;
    private UUID negociacionId;
    private String tipo;
    private String url;
    private LocalDateTime createdAt;
}
