package com.felipeparodi.crmbackend.documentacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record ComparacionDocumentosIA(
        @JsonPropertyDescription("Resumen en 2-4 frases de las diferencias globales entre el documento A y el documento B, en espanol, dirigido a un asesor inmobiliario que necesita entender rapido que cambio entre las dos versiones.")
        String resumenGeneral,

        @JsonPropertyDescription("Nivel de cambio global entre los dos documentos: SIN_CAMBIOS (son iguales en sustancia), MENOR (diferencias de forma o detalles sin impacto juridico/economico), MODERADO (algunos cambios relevantes) o MAYOR (cambios sustanciales en precio, partes, plazos u obligaciones).")
        String nivelCambio,

        @JsonPropertyDescription("Lista de todas las diferencias detectadas entre el documento A y el documento B. Lista vacia si los documentos son sustancialmente iguales.")
        List<DiferenciaDocumentoIA> diferencias
) {
}
