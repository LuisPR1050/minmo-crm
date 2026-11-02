package com.felipeparodi.crmbackend.documentacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroPromesaCompraventa {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('promesa_compraventa_numero_seq')")
                .getSingleResult())
                .longValue();

        return "PROM-" + String.format("%06d", siguienteValor);
    }
}
