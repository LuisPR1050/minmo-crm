package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class CopiaVideoCanalResponse {

    private UUID id;
    private String canalNombre;
    private String texto;
}
