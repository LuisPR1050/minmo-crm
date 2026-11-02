package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ActualizarPromesaCompraventaRequest {

    private String ciudad;
    private String opcionFirma;
    private String textoPromesa;
}
