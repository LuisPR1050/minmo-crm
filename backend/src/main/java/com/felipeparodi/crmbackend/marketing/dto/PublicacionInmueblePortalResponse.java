package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PublicacionInmueblePortalResponse {

    private UUID id;
    private UUID portalId;
    private String portalNombre;
    private String estado;
}
