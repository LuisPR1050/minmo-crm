package com.felipeparodi.crmbackend.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LoginResponse {

    private String token;
    private UUID usuarioId;
    private String nombre;
    private String email;
    private String rol;
}
