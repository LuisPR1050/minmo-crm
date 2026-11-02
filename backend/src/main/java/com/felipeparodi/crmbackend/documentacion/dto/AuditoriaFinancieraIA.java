package com.felipeparodi.crmbackend.documentacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record AuditoriaFinancieraIA(
        @JsonPropertyDescription("Resumen en 1-2 frases del estado financiero de la negociacion frente a lo pactado en el plan de pago.")
        String resumenGeneral,

        @JsonPropertyDescription("Lista de inconsistencias detectadas entre los recibos de pago registrados y el plan de pago pactado. Lista vacia si no se detecto ninguna.")
        List<HallazgoRevisionIA> hallazgos,

        @JsonPropertyDescription("true si los pagos registrados son consistentes con el plan de pago pactado, false si hay al menos una inconsistencia relevante (severidad MEDIA o ALTA).")
        boolean consistente
) {
}
