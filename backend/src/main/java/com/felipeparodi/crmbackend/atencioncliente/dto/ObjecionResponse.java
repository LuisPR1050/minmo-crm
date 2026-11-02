package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ObjecionResponse {

    private UUID id;
    private String nombre;
}