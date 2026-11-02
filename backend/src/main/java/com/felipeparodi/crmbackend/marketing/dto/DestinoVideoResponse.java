package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class DestinoVideoResponse {

    private UUID id;
    private String nombre;
}
