package com.felipeparodi.crmbackend.captacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneradorCodigoInmuebleTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private GeneradorCodigoInmueble generador;

    @ParameterizedTest
    @CsvSource({
            "CASA,CAS",
            "APARTAMENTO,APT",
            "CASA_LOTE,CAL",
            "LOTE,LOT"
    })
    void usaElPrefijoCorrectoPorTipoDePropiedadConocido(String tipoPropiedad, String prefijoEsperado) {
        when(entityManager.createNativeQuery("SELECT nextval('inmueble_codigo_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(7L);

        String codigo = generador.generar(tipoPropiedad);

        assertThat(codigo).isEqualTo(prefijoEsperado + "-000007");
    }

    @Test
    void usaPrefijoGenericoParaTipoDePropiedadDesconocido() {
        when(entityManager.createNativeQuery("SELECT nextval('inmueble_codigo_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(3L);

        String codigo = generador.generar("BODEGA");

        assertThat(codigo).isEqualTo("INM-000003");
    }

}
