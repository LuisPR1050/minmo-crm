package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class EstadisticaPortalResponse {

    private UUID id;
    private Integer vistas;
    private Integer contactos;
    private LocalDate fechaCorte;
}
