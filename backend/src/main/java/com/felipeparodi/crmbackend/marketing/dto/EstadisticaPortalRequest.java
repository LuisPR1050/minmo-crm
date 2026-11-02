package com.felipeparodi.crmbackend.marketing.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class EstadisticaPortalRequest {

    private Integer vistas;
    private Integer contactos;
    private LocalDate fechaCorte;
}
