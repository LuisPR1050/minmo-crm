package com.felipeparodi.crmbackend.captacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record PrecioSugeridoIA(
        @JsonPropertyDescription("Precio de venta sugerido en pesos colombianos (COP), numero completo sin abreviar, basado en los inmuebles comparables.")
        Long precioSugerido,

        @JsonPropertyDescription("Precio minimo razonable del rango sugerido, en COP.")
        Long rangoMinimo,

        @JsonPropertyDescription("Precio maximo razonable del rango sugerido, en COP.")
        Long rangoMaximo,

        @JsonPropertyDescription("Justificacion breve (2-4 frases) explicando como se llego a este precio a partir de los comparables.")
        String justificacion
) {
}
