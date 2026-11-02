package com.felipeparodi.crmbackend.documentacion.exception;

public class ServicioIANoDisponibleException extends RuntimeException {

    public ServicioIANoDisponibleException(String mensaje, Throwable causa) {
        super(mensaje, causa);
    }
}
