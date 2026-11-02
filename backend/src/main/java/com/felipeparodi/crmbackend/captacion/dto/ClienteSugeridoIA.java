package com.felipeparodi.crmbackend.captacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ClienteSugeridoIA(
        @JsonPropertyDescription("Referencia exacta del cliente candidato, tal como aparece en la lista (ej: REF-1).")
        String referencia,

        @JsonPropertyDescription("Motivo breve (1-2 frases) de por que este cliente podria estar interesado en la propiedad nueva.")
        String motivo
) {
}
