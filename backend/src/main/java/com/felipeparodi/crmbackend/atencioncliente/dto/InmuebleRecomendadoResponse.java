package com.felipeparodi.crmbackend.atencioncliente.dto;

import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
public class InmuebleRecomendadoResponse {

    private InmuebleResponse inmueble;
    private String motivo;
}
