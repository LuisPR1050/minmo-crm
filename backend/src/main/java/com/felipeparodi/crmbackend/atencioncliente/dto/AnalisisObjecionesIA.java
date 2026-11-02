package com.felipeparodi.crmbackend.atencioncliente.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record AnalisisObjecionesIA(
        @JsonPropertyDescription("Resumen en 2-4 frases de las principales objeciones/dudas de este cliente detectadas en sus visitas.")
        String resumen,

        @JsonPropertyDescription("Lista de 2-5 recomendaciones concretas y accionables para que el asesor aborde estas objeciones y avance el cierre del negocio.")
        List<String> recomendaciones
) {
}
