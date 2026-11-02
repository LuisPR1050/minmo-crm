package com.felipeparodi.crmbackend.negociacion.util;

import jakarta.persistence.EntityManager;
import jakarta.persistence.Query;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GeneradorNumeroNegocioTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private GeneradorNumeroNegocio generador;

    @Test
    void generaNumeroConPrefijoNegYConsecutivoDeSeisDigitos() {
        when(entityManager.createNativeQuery("SELECT nextval('negocio_numero_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(42L);

        String numero = generador.generar();

        assertThat(numero).isEqualTo("NEG-000042");
    }

    @Test
    void rellenaConCerosAIzquierdaParaValoresPequenos() {
        when(entityManager.createNativeQuery("SELECT nextval('negocio_numero_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1L);

        assertThat(generador.generar()).isEqualTo("NEG-000001");
    }

    @Test
    void noTruncaValoresConMasDeSeisDigitos() {
        when(entityManager.createNativeQuery("SELECT nextval('negocio_numero_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(1234567L);

        assertThat(generador.generar()).isEqualTo("NEG-1234567");
    }
}
