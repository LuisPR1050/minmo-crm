package com.felipeparodi.crmbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class UsuarioResponse {

    private UUID id;
    private String nombre;
    private String email;
    private String telefono;
    private String rol;
    private boolean activo;
    private LocalDateTime createdAt;
}
