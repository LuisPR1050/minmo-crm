package com.felipeparodi.crmbackend.cucumber;

import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.ClienteService;
import com.felipeparodi.crmbackend.marketing.dto.LeadPortalWebhookRequest;
import com.felipeparodi.crmbackend.marketing.repository.EstadisticaPortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import com.felipeparodi.crmbackend.marketing.service.LeadPortalWebhookService;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class LeadPortalWebhookSteps {

    private ClienteService clienteService;
    private LeadPortalWebhookService leadPortalWebhookService;

    private LeadPortalWebhookRequest request;
    private ClienteResponse clienteCreado;
    private IllegalArgumentException errorCapturado;

    @Before
    public void configurarMocks() {
        clienteService = mock(ClienteService.class);
        PublicacionInmuebleRepository publicacionInmuebleRepository = mock(PublicacionInmuebleRepository.class);
        PublicacionInmueblePortalRepository publicacionInmueblePortalRepository =
                mock(PublicacionInmueblePortalRepository.class);
        EstadisticaPortalRepository estadisticaPortalRepository = mock(EstadisticaPortalRepository.class);
        HistorialPublicacionRepository historialPublicacionRepository = mock(HistorialPublicacionRepository.class);

        leadPortalWebhookService = new LeadPortalWebhookService(clienteService, publicacionInmuebleRepository,
                publicacionInmueblePortalRepository, estadisticaPortalRepository, historialPublicacionRepository);

        request = new LeadPortalWebhookRequest();
        request.setPortalNombre("Fincaraiz");
    }

    @Dado("un contacto de portal con nombre {string} y telefono {string}")
    public void unContactoConNombreYTelefono(String nombre, String telefono) {
        request.setNombreContacto(nombre);
        request.setTelefonoContacto(telefono);
    }

    @Dado("un contacto de portal con nombre {string}, telefono {string} y correo {string}")
    public void unContactoConNombreTelefonoYCorreo(String nombre, String telefono, String correo) {
        request.setNombreContacto(nombre);
        request.setTelefonoContacto(telefono);
        request.setCorreoContacto(correo);
    }

    @Cuando("se recibe el contacto")
    public void seRecibeElContacto() {
        clienteCreado = ClienteResponse.builder().id(UUID.randomUUID()).nombre(request.getNombreContacto()).build();
        when(clienteService.crear(any(ClienteRequest.class))).thenReturn(clienteCreado);

        try {
            clienteCreado = leadPortalWebhookService.recibirContacto(request);
            errorCapturado = null;
        } catch (IllegalArgumentException ex) {
            errorCapturado = ex;
            clienteCreado = null;
        }
    }

    @Entonces("la recepción es rechazada con un error que menciona {string}")
    public void laRecepcionEsRechazadaConUnErrorQueMenciona(String fragmentoEsperado) {
        assertThat(errorCapturado).isNotNull();
        assertThat(errorCapturado.getMessage().toLowerCase()).contains(fragmentoEsperado.toLowerCase());
    }

    @Entonces("se crea un cliente con nombre {string}")
    public void seCreaUnClienteConNombre(String nombreEsperado) {
        assertThat(errorCapturado).isNull();
        assertThat(clienteCreado).isNotNull();
        assertThat(clienteCreado.getNombre()).isEqualTo(nombreEsperado);
    }
}
