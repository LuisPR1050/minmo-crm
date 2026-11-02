package com.felipeparodi.crmbackend.documentacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroActaEntrega {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('acta_entrega_numero_seq')")
                .getSingleResult())
                .longValue();

        return "ACT-" + String.format("%06d", siguienteValor);
    }
}
