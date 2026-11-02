package com.felipeparodi.crmbackend.documentacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroOtrosiPromesa {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('otrosi_promesa_numero_seq')")
                .getSingleResult())
                .longValue();

        return "OTR-" + String.format("%06d", siguienteValor);
    }
}
