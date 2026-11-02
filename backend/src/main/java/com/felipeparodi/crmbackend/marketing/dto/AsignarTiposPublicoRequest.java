package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class AsignarTiposPublicoRequest {

    private List<UUID> tipoPublicoIds;
}
