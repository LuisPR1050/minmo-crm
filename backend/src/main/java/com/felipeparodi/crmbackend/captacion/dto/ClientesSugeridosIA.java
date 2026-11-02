package com.felipeparodi.crmbackend.captacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record ClientesSugeridosIA(
        @JsonPropertyDescription("Hasta 5 clientes sugeridos, ordenados del que tiene mayor probabilidad de interes al que tiene menor. Lista vacia si ninguno encaja razonablemente.")
        List<ClienteSugeridoIA> sugerencias
) {
}
