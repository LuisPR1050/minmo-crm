package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.ClienteService;
import com.felipeparodi.crmbackend.marketing.dto.LeadPortalWebhookRequest;
import com.felipeparodi.crmbackend.marketing.repository.EstadisticaPortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class LeadPortalWebhookServiceTest {

    @Mock
    private ClienteService clienteService;
    @Mock
    private PublicacionInmuebleRepository publicacionInmuebleRepository;
    @Mock
    private PublicacionInmueblePortalRepository publicacionInmueblePortalRepository;
    @Mock
    private EstadisticaPortalRepository estadisticaPortalRepository;
    @Mock
    private HistorialPublicacionRepository historialPublicacionRepository;

    private LeadPortalWebhookService leadPortalWebhookService;

    @BeforeEach
    void setUp() {
        leadPortalWebhookService = new LeadPortalWebhookService(clienteService, publicacionInmuebleRepository,
                publicacionInmueblePortalRepository, estadisticaPortalRepository, historialPublicacionRepository);
    }

    private LeadPortalWebhookRequest requestValido() {
        LeadPortalWebhookRequest request = new LeadPortalWebhookRequest();
        request.setNombreContacto("Maria Perez");
        request.setTelefonoContacto("3001234567");
        request.setCorreoContacto("maria@example.com");
        request.setPortalNombre("Fincaraiz");
        return request;
    }

    @Test
    void recibirContactoCreaElClienteCuandoLosDatosSonValidos() {
        LeadPortalWebhookRequest request = requestValido();

        ClienteResponse clienteCreado = ClienteResponse.builder()
                .id(UUID.randomUUID())
                .nombre(request.getNombreContacto())
                .build();

        when(clienteService.crear(any(ClienteRequest.class))).thenReturn(clienteCreado);

        ClienteResponse response = leadPortalWebhookService.recibirContacto(request);

        assertThat(response.getNombre()).isEqualTo("Maria Perez");
        verify(clienteService).crear(any(ClienteRequest.class));
    }

    @Test
    void recibirContactoLanzaExcepcionCuandoElNombreEstaVacio() {
        LeadPortalWebhookRequest request = requestValido();
        request.setNombreContacto("  ");

        assertThatThrownBy(() -> leadPortalWebhookService.recibirContacto(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("nombre");

        verify(clienteService, never()).crear(any());
    }

    @Test
    void recibirContactoLanzaExcepcionCuandoElNombreEsNulo() {
        LeadPortalWebhookRequest request = requestValido();
        request.setNombreContacto(null);

        assertThatThrownBy(() -> leadPortalWebhookService.recibirContacto(request))
                .isInstanceOf(IllegalArgumentException.class);

        verify(clienteService, never()).crear(any());
    }

    @Test
    void recibirContactoLanzaExcepcionCuandoElTelefonoEstaVacio() {
        LeadPortalWebhookRequest request = requestValido();
        request.setTelefonoContacto("");

        assertThatThrownBy(() -> leadPortalWebhookService.recibirContacto(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("telefono");

        verify(clienteService, never()).crear(any());
    }

    @Test
    void recibirContactoLanzaExcepcionCuandoElCorreoTieneFormatoInvalido() {
        LeadPortalWebhookRequest request = requestValido();
        request.setCorreoContacto("no-es-un-correo");

        assertThatThrownBy(() -> leadPortalWebhookService.recibirContacto(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("correo");

        verify(clienteService, never()).crear(any());
    }

    @Test
    void recibirContactoAceptaCorreoNuloOVacio() {
        LeadPortalWebhookRequest request = requestValido();
        request.setCorreoContacto(null);

        ClienteResponse clienteCreado = ClienteResponse.builder().id(UUID.randomUUID()).nombre("Maria Perez").build();
        when(clienteService.crear(any(ClienteRequest.class))).thenReturn(clienteCreado);

        assertThat(leadPortalWebhookService.recibirContacto(request)).isNotNull();
    }
}
