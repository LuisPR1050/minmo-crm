package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AsignarAutorizacionesPropietarioRequest {

    private List<UUID> autorizacionIds;
}
