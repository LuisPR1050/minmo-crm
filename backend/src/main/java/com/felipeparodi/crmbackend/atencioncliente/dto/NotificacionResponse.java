package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class NotificacionResponse {

    private UUID id;
    private String tipo;
    private String mensaje;
    private UUID referenciaId;
    private boolean leida;
    private LocalDateTime createdAt;
}