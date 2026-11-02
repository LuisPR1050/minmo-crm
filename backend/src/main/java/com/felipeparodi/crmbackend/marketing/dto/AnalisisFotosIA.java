package com.felipeparodi.crmbackend.marketing.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record AnalisisFotosIA(
        @JsonPropertyDescription("Puntuacion general de 1 a 10 sobre que tan listas estan estas fotos para publicar (10 = excelentes, listas para publicar tal cual).")
        Integer puntuacionGeneral,

        @JsonPropertyDescription("Lista de 1 a 4 fortalezas de las fotos actuales.")
        List<String> fortalezas,

        @JsonPropertyDescription("Lista de problemas detectados (mala iluminacion, desorden, angulos pobres, resolucion baja, objetos personales visibles, etc). Lista vacia si no hay problemas relevantes.")
        List<String> problemas,

        @JsonPropertyDescription("Lista de 1 a 5 recomendaciones concretas y accionables para mejorar el set de fotos antes de publicar.")
        List<String> recomendaciones,

        @JsonPropertyDescription("Espacios o tomas que parecen faltar y serian valiosos para la publicacion (ej: fachada, cocina, bano, fotos de dia). Lista vacia si el set ya esta completo.")
        List<String> fotosFaltantes
) {
}
