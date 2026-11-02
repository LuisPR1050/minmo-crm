package com.felipeparodi.crmbackend.marketing.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record DescripcionesGeneradasIA(
        @JsonPropertyDescription("Descripcion corta para redes sociales y portales que priorizan brevedad (ej. Facebook Marketplace). Maximo 180 caracteres, un solo parrafo, sin emojis excesivos, en espanol neutro.")
        String descripcionCorta,

        @JsonPropertyDescription("Descripcion larga y completa para la pagina web propia de la inmobiliaria. 3 a 4 parrafos, tono profesional y calido, resalta ubicacion, distribucion y caracteristicas destacadas.")
        String descripcionLarga,

        @JsonPropertyDescription("Descripcion optimizada para portales inmobiliarios como Fincaraiz o Metrocuadrado. 2 parrafos, incluye palabras clave de busqueda relevantes (tipo de propiedad, zona, ciudad, numero de habitaciones) de forma natural.")
        String descripcionOptimizada
) {
}
