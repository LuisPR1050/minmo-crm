package com.felipeparodi.crmbackend.captacion.dto;

import com.fasterxml.jackson.annotation.JsonPropertyDescription;

import java.util.List;

public record FiltrosBusquedaInmuebleIA(
        @JsonPropertyDescription("Tipo de propiedad mencionado: CASA, APARTAMENTO, CASA_LOTE o LOTE. Cadena vacia \"\" si no se menciona.")
        String tipoPropiedad,

        @JsonPropertyDescription("Ciudad mencionada. Cadena vacia \"\" si no se menciona.")
        String ciudad,

        @JsonPropertyDescription("Zona o barrio de Valledupar mencionado (ej: Norte, Novalito, Centro norte, Centro, Garupal - Las Flores, Batallon la Popa, Comuna 4, Mayales, UPC, Las Americas, Rural, Campestre). Cadena vacia \"\" si no se menciona.")
        String zona,

        @JsonPropertyDescription("Puntos de referencia o lugares cercanos mencionados tal como los dijo el usuario (ej: 'centro comercial mayales', 'la popa', 'una iglesia'). Lista vacia si no menciona ninguno.")
        List<String> puntosReferencia,

        @JsonPropertyDescription("Tipo de entorno mencionado: Barrio, Urbanizacion, Conjunto Cerrado, Edificio, Condominio o Reserva campestre. Cadena vacia \"\" si no se menciona.")
        String entorno,

        @JsonPropertyDescription("Precio minimo en pesos colombianos (COP), en numero completo sin abreviar (ej: '400 millones' = 400000000). 0 si no se menciona un minimo explicito.")
        long precioMinimo,

        @JsonPropertyDescription("Precio maximo en pesos colombianos (COP), en numero completo sin abreviar. Si el usuario menciona un solo precio sin aclarar si es minimo o maximo (ej: 'con un precio de 400 millones'), usalo como precio maximo. 0 si no se menciona.")
        long precioMaximo,

        @JsonPropertyDescription("Numero minimo de habitaciones mencionado (si dice 'tres habitaciones' es minimo 3). 0 si no se menciona.")
        int habitacionesMinimo,

        @JsonPropertyDescription("Numero minimo de banos mencionado. 0 si no se menciona.")
        int banosMinimo,

        @JsonPropertyDescription("Estrato socioeconomico (1 a 7) mencionado. 0 si no se menciona.")
        int estrato,

        @JsonPropertyDescription("Area minima en metros cuadrados mencionada. 0 si no se menciona.")
        double areaMinima,

        @JsonPropertyDescription("Estado juridico mencionado: LIBRE, HIPOTECA, LEASING, PATRIMONIO_DE_FAMILIA, VIVIENDA_FAMILIAR o EMBARGO. Cadena vacia \"\" si no se menciona.")
        String estadoJuridico,

        @JsonPropertyDescription("Estado de captacion mencionado: PROSPECTO, CONTACTADO, VISITADO, CAPTADO_CON_MATERIAL, CAPTADO_SIN_MATERIAL, EN_NEGOCIACION, VENDIDO, NEGOCIO_PERDIDO, DESCARTADO. Cadena vacia \"\" si no se menciona.")
        String estadoCaptacion
) {
}
