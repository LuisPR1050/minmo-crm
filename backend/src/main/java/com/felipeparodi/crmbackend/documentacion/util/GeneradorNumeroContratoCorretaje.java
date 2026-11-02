package com.felipeparodi.crmbackend.documentacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorNumeroContratoCorretaje {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('contrato_corretaje_numero_seq')")
                .getSingleResult())
                .longValue();

        return "CORR-" + String.format("%06d", siguienteValor);
    }
}
