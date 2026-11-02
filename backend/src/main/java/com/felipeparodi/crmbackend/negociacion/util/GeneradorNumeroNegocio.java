package com.felipeparodi.crmbackend.negociacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroNegocio {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('negocio_numero_seq')")
                .getSingleResult())
                .longValue();

        return "NEG-" + String.format("%06d", siguienteValor);
    }
}