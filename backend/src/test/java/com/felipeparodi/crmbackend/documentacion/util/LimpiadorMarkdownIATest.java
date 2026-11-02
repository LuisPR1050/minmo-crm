package com.felipeparodi.crmbackend.documentacion.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class LimpiadorMarkdownIATest {

    @Test
    void quitaDoblesAsteriscosDeNegrilla() {
        String texto = "**CLAUSULA PRIMERA**: Objeto del contrato.";

        assertThat(LimpiadorMarkdownIA.limpiar(texto)).isEqualTo("CLAUSULA PRIMERA: Objeto del contrato.");
    }

    @Test
    void quitaNumeralesDeEncabezado() {
        String texto = "# CLAUSULA PRIMERA\n## Objeto\nTexto normal.";

        assertThat(LimpiadorMarkdownIA.limpiar(texto)).isEqualTo("CLAUSULA PRIMERA\nObjeto\nTexto normal.");
    }

    @Test
    void convierteVinetasDeAsteriscoAGuion() {
        String texto = "Bienes incluidos:\n* Nevera\n* Estufa";

        assertThat(LimpiadorMarkdownIA.limpiar(texto)).isEqualTo("Bienes incluidos:\n- Nevera\n- Estufa");
    }

    @Test
    void devuelveNuloSiElTextoEsNulo() {
        assertThat(LimpiadorMarkdownIA.limpiar(null)).isNull();
    }
}
