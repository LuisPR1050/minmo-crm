package com.felipeparodi.crmbackend.captacion.util;

import org.springframework.stereotype.Component;

import java.util.Map;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;

@Component
public class GeneradorCodigoInmueble {

    private static final Map<String, String> PREFIJOS = Map.of(
            "CASA", "CAS",
            "APARTAMENTO", "APT",
            "CASA_LOTE", "CAL",
            "LOTE", "LOT",
            "CASA_CAMPESTRE", "CAC"
    );

    @PersistenceContext
    private EntityManager entityManager;

    public String generar(String tipoPropiedad) {
        String prefijo = PREFIJOS.getOrDefault(tipoPropiedad, "INM");

        Long siguienteValor = ((Number) entityManager
                .createNativeQuery("SELECT nextval('inmueble_codigo_seq')")
                .getSingleResult())
                .longValue();

        String consecutivo = String.format("%06d", siguienteValor);

        return prefijo + "-" + consecutivo;
    }
}
