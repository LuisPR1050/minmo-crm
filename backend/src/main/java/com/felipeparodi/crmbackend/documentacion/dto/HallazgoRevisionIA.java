package com.felipeparodi.crmbackend.documentacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record HallazgoRevisionIA(
        @JsonPropertyDescription("Descripcion clara y concreta del hallazgo o inconsistencia detectada, en espanol.")
        String descripcion,

        @JsonPropertyDescription("Severidad del hallazgo: BAJA, MEDIA o ALTA.")
        String severidad
) {
}
