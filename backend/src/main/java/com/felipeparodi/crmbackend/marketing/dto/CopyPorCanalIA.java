package com.felipeparodi.crmbackend.marketing.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record CopyPorCanalIA(
        @JsonPropertyDescription("Nombre exacto del canal, tal como aparece en la lista de canales seleccionados que se te proporciono.")
        String canal,

        @JsonPropertyDescription("Texto de la publicacion (caption) adaptado al formato y tono habitual de ese canal especifico.")
        String texto
) {
}
