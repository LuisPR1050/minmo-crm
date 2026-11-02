package com.felipeparodi.crmbackend.captacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AsignarCaracteristicasRequest {

    private List<UUID> caracteristicaIds;
}