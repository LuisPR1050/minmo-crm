package com.felipeparodi.crmbackend.documentacion.util;

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
class GeneradorNumeroActaEntregaTest {

    @Mock
    private EntityManager entityManager;

    @Mock
    private Query query;

    @InjectMocks
    private GeneradorNumeroActaEntrega generador;

    @Test
    void generaNumeroConPrefijoActYConsecutivoDeSeisDigitos() {
        when(entityManager.createNativeQuery("SELECT nextval('acta_entrega_numero_seq')")).thenReturn(query);
        when(query.getSingleResult()).thenReturn(15L);

        assertThat(generador.generar()).isEqualTo("ACT-000015");
    }
}
