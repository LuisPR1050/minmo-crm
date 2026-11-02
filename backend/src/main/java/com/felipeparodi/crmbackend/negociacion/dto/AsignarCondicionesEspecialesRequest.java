package com.felipeparodi.crmbackend.negociacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AsignarCondicionesEspecialesRequest {

    private List<UUID> condicionEspecialIds;
}