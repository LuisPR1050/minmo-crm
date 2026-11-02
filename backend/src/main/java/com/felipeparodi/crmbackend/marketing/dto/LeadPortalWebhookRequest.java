package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class LeadPortalWebhookRequest {

    private UUID publicacionInmuebleId;
    private String portalNombre;
    private String nombreContacto;
    private String telefonoContacto;
    private String correoContacto;
    private String mensaje;
}
