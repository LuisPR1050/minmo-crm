package com.felipeparodi.crmbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Unico catch-all de excepciones de toda la aplicacion. Cada modulo tiene su propio
 * {@code @RestControllerAdvice} para sus excepciones especificas (recurso no encontrado,
 * validacion, etc.), pero un catch-all de {@code Exception.class} NO debe duplicarse en
 * cada uno: Spring solo consulta el primer advice bean que tenga algun handler que
 * coincida con la excepcion, así que un catch-all repetido en varios advices intercepta
 * excepciones especificas de otros modulos antes de que lleguen a su handler correcto.
 * Por la misma razon, este bean se marca con {@code @Order(LOWEST_PRECEDENCE)}: sin eso,
 * Spring resuelve los advices en el orden en que los escanea (alfabetico por paquete), y
 * este catch-all terminaba interceptando excepciones de modulos cuyo paquete ordena
 * despues de "config" (documentacion, marketing, negociacion) antes de que llegaran a su
 * handler especifico.
 */
@RestControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
public class ManejadorGlobalExcepcionGenerica {

    private static final Logger log = LoggerFactory.getLogger(ManejadorGlobalExcepcionGenerica.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> manejarErrorInesperado(Exception ex) {
        // Excepciones nativas de Spring (recurso estatico no encontrado, metodo HTTP no
        // soportado, etc.) implementan ErrorResponse y ya traen su propio status code
        // correcto -- no son "errores inesperados" y no deben convertirse en 500.
        // ErrorResponse es una interfaz (no una subclase comun de Throwable), por eso
        // el chequeo va dentro del catch-all en vez de en un @ExceptionHandler separado.
        if (ex instanceof ErrorResponse errorResponse) {
            return construirRespuesta(errorResponse.getStatusCode(),
                    errorResponse.getBody().getDetail() != null ? errorResponse.getBody().getDetail() : ex.getMessage());
        }

        log.error("Error inesperado al procesar la solicitud", ex);

        return construirRespuesta(HttpStatus.INTERNAL_SERVER_ERROR, "Ocurrio un error inesperado al procesar la solicitud");
    }

    private ResponseEntity<Map<String, Object>> construirRespuesta(HttpStatusCode status, String mensaje) {
        Map<String, Object> cuerpo = new LinkedHashMap<>();
        cuerpo.put("timestamp", LocalDateTime.now());
        cuerpo.put("status", status.value());
        cuerpo.put("error", HttpStatus.resolve(status.value()) != null
                ? HttpStatus.resolve(status.value()).getReasonPhrase()
                : "Error");
        cuerpo.put("mensaje", mensaje);

        return ResponseEntity.status(status).body(cuerpo);
    }
}
