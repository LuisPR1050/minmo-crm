package com.felipeparodi.crmbackend.atencioncliente.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

public record ResumenVisitaIA(
        @JsonPropertyDescription("Resumen profesional y neutral de la visita (3-6 frases), redactado para compartir con el propietario del inmueble: quien visito, cuando, nivel de interes general y proximos pasos. No incluyas datos de contacto del cliente ni criticas innecesarias sobre el inmueble.")
        String resumen
) {
}
