package com.felipeparodi.crmbackend.captacion.publico.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LandingFotoResponse {

    private UUID id;
    private String url;
    private boolean esPrincipal;
}
