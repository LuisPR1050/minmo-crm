package com.felipeparodi.crmbackend.atencioncliente.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record InmuebleRecomendadoIA(
        @JsonPropertyDescription("Codigo exacto del inmueble recomendado, tal como aparece en la lista de candidatos (ej: CAS-001).")
        String codigoInmueble,

        @JsonPropertyDescription("Motivo breve (1-2 frases) de por que este inmueble encaja con lo que busca el cliente.")
        String motivo
) {
}
