package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class AsignarItemEntregaRequest {

    private UUID itemEntregaId;
    private boolean incluido;
}