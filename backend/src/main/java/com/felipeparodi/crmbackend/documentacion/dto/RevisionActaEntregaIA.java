package com.felipeparodi.crmbackend.documentacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record RevisionActaEntregaIA(
        @JsonPropertyDescription("Resumen en 1-2 frases del estado general de la entrega y su consistencia interna.")
        String resumenGeneral,

        @JsonPropertyDescription("Lista de inconsistencias o contradicciones detectadas en la informacion del acta (por ejemplo, declaracion de conformidad que contradice danos reportados, o hitos de la negociacion que no coinciden con la entrega). Lista vacia si no se detecto ninguna.")
        List<HallazgoRevisionIA> hallazgos,

        @JsonPropertyDescription("true si la informacion del acta es consistente y esta lista para firmarse, false si hay al menos una inconsistencia relevante (severidad MEDIA o ALTA) que deberia revisarse antes de firmar.")
        boolean listaParaFirmar
) {
}
