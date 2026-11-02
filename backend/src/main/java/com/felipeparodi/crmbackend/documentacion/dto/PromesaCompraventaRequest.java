package com.felipeparodi.crmbackend.documentacion.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class PromesaCompraventaRequest {

    @NotNull(message = "Selecciona una negociacion")
    private UUID negociacionId;
    private LocalDate fechaElaboracion;
    private String ciudad;
    private String opcionFirma;
}
