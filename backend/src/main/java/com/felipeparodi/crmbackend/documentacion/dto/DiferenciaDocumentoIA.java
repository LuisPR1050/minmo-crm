package com.felipeparodi.crmbackend.documentacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record DiferenciaDocumentoIA(
        @JsonPropertyDescription("Nombre de la clausula, seccion o campo donde se encuentra la diferencia (por ejemplo: 'Precio de venta', 'Clausula quinta - forma de pago', 'Fecha de entrega').")
        String seccion,

        @JsonPropertyDescription("Que dice o establece el documento A en esa seccion. Si la seccion no existe en el documento A, escribe 'No aparece en este documento'.")
        String contenidoDocumentoA,

        @JsonPropertyDescription("Que dice o establece el documento B en esa seccion. Si la seccion no existe en el documento B, escribe 'No aparece en este documento'.")
        String contenidoDocumentoB,

        @JsonPropertyDescription("Tipo de cambio: AGREGADO (existe solo en el documento B), ELIMINADO (existe solo en el documento A) o MODIFICADO (existe en ambos pero con contenido distinto).")
        String tipoCambio,

        @JsonPropertyDescription("Relevancia juridica o economica de la diferencia para las partes: BAJA, MEDIA o ALTA.")
        String relevancia
) {
}
