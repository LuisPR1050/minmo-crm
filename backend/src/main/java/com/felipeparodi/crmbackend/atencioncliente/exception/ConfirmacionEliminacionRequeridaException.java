package com.felipeparodi.crmbackend.atencioncliente.exception;

import java.util.List;

/**
 * Se lanza cuando eliminar un registro borraria tambien informacion relacionada "operativa"
 * (visitas, requerimientos, etc.) y el llamador todavia no confirmo que quiere continuar.
 * El controller expone esto como un 409 con el resumen de lo que se perderia, para que el
 * frontend le muestre la advertencia al usuario antes de reintentar con confirmar=true.
 */
public class ConfirmacionEliminacionRequeridaException extends RuntimeException {

    private final List<String> resumen;

    public ConfirmacionEliminacionRequeridaException(List<String> resumen) {
        super("Se requiere confirmacion: esta accion eliminara informacion relacionada");
        this.resumen = resumen;
    }

    public List<String> getResumen() {
        return resumen;
    }
}
