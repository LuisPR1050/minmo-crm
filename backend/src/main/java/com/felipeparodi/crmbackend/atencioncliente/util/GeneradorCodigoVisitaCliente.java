package com.felipeparodi.crmbackend.atencioncliente.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Component;

@Component
public class GeneradorCodigoVisitaCliente {

    @PersistenceContext
    private EntityManager entityManager;

    public String generar() {
        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('visita_cliente_codigo_seq')")
                .getSingleResult())
                .longValue();

        return "VIS-" + String.format("%06d", siguienteValor);
    }
}
