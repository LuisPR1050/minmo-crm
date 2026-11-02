package com.felipeparodi.crmbackend.atencioncliente.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record RecomendacionInmuebleIA(
        @JsonPropertyDescription("Hasta 5 inmuebles recomendados, ordenados del que mejor encaja al que menos, segun los candidatos disponibles. Lista vacia si ninguno encaja razonablemente.")
        List<InmuebleRecomendadoIA> recomendaciones
) {
}
