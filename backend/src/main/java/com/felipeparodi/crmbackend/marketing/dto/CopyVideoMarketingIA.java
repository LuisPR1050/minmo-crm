package com.felipeparodi.crmbackend.marketing.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record CopyVideoMarketingIA(
        @JsonPropertyDescription("Titulo llamativo para el video, maximo 60 caracteres.")
        String titulo,

        @JsonPropertyDescription("Subtitulo o gancho complementario al titulo, maximo 100 caracteres.")
        String subtitulo,

        @JsonPropertyDescription("Descripcion/caption principal del video, adaptada a la duracion objetivo indicada (en minutos) si se especifico una.")
        String descripcion,

        @JsonPropertyDescription("Frase de llamado a la accion, redactada de forma persuasiva a partir del tipo de llamado a la accion indicado (por ejemplo, si el tipo es AGENDA_VISITA, algo como 'Agenda tu visita hoy mismo').")
        String llamadoAccionTexto,

        @JsonPropertyDescription("Lista de 5 a 10 hashtags relevantes en espanol, sin el simbolo #, relacionados con el tipo de propiedad, la ciudad/zona y el objetivo de la publicacion.")
        List<String> hashtags,

        @JsonPropertyDescription("Una variante del texto (caption) adaptada a cada canal de publicacion seleccionado, respetando el formato y tono tipico de cada uno (por ejemplo, Instagram Stories es breve, WhatsApp es directo y personal, Facebook admite mas texto).")
        List<CopyPorCanalIA> copiasPorCanal
) {
}
