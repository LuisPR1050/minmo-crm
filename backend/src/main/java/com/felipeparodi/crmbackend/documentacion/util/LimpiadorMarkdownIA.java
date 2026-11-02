package com.felipeparodi.crmbackend.documentacion.util;

/**
 * Quita artefactos de formato Markdown (negrillas con **, encabezados con #, vinetas con *)
 * que a veces aparecen en el texto generado por la IA, para que el documento final quede en
 * texto plano limpio, listo para mostrarse y convertirse a PDF.
 */
public final class LimpiadorMarkdownIA {

    private LimpiadorMarkdownIA() {
    }

    public static String limpiar(String texto) {
        if (texto == null) {
            return null;
        }

        return texto
                .replace("**", "")
                .replaceAll("(?m)^#{1,6}\\s*", "")
                .replaceAll("(?m)^\\*\\s+", "- ");
    }
}
