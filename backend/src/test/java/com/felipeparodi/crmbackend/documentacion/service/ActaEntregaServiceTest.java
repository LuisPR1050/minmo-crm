package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroActaEntrega;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.service.NegociacionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ActaEntregaServiceTest {

    @Mock
    private ActaEntregaRepository actaEntregaRepository;
    @Mock
    private NegociacionRepository negociacionRepository;
    @Mock
    private PromesaCompraventaRepository promesaCompraventaRepository;
    @Mock
    private ParteNegociacionRepository parteNegociacionRepository;
    @Mock
    private HistorialNegociacionRepository historialNegociacionRepository;
    @Mock
    private GeneradorNumeroActaEntrega generadorNumeroActaEntrega;
    @Mock
    private NegociacionService negociacionService;

    private ActaEntregaService actaEntregaService;

    @BeforeEach
    void setUp() {
        actaEntregaService = new ActaEntregaService(actaEntregaRepository, negociacionRepository,
                promesaCompraventaRepository, parteNegociacionRepository, historialNegociacionRepository,
                generadorNumeroActaEntrega, negociacionService);
    }

    private Negociacion negociacionCompleta(UUID id) {
        Inmueble inmueble = Inmueble.builder()
                .id(UUID.randomUUID())
                .codigo("APT-000001")
                .direccionExacta("Calle 1 # 2-3")
                .ciudad("Bogota")
                .build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).nombre("Comprador").telefono("3000000000").build();
        Propietario propietario = Propietario.builder().id(UUID.randomUUID()).nombre("Vendedor")
                .telefonoPrincipal("3000000001").build();

        return Negociacion.builder()
                .id(id)
                .numeroNegocio("NEG-000001")
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .build();
    }

    @Test
    void crearGeneraElActaYMarcaLaEntregaComoRealizadaEnLaNegociacion() {
        UUID negociacionId = UUID.randomUUID();
        Negociacion negociacion = negociacionCompleta(negociacionId);

        ActaEntregaRequest request = new ActaEntregaRequest();
        request.setNegociacionId(negociacionId);

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
        when(actaEntregaRepository.findByNegociacionId(negociacionId)).thenReturn(Optional.empty());
        when(generadorNumeroActaEntrega.generar()).thenReturn("ACT-000001");
        when(actaEntregaRepository.save(any(ActaEntrega.class))).thenAnswer(inv -> {
            ActaEntrega acta = inv.getArgument(0);
            acta.setId(UUID.randomUUID());
            return acta;
        });
        when(parteNegociacionRepository.findByNegociacionId(negociacionId)).thenReturn(Collections.emptyList());

        ActaEntregaResponse response = actaEntregaService.crear(request);

        assertThat(response.getNumeroActa()).isEqualTo("ACT-000001");
        assertThat(response.getNegociacionId()).isEqualTo(negociacionId);

        verify(negociacionService).marcarEntregaRealizada(eq(negociacionId), any(MarcarHitoRequest.class));
        verify(historialNegociacionRepository).save(any());
    }

    @Test
    void crearLanzaExcepcionSiYaExisteUnActaParaLaNegociacion() {
        UUID negociacionId = UUID.randomUUID();
        Negociacion negociacion = negociacionCompleta(negociacionId);

        ActaEntregaRequest request = new ActaEntregaRequest();
        request.setNegociacionId(negociacionId);

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
        when(actaEntregaRepository.findByNegociacionId(negociacionId))
                .thenReturn(Optional.of(ActaEntrega.builder().id(UUID.randomUUID()).build()));

        assertThatThrownBy(() -> actaEntregaService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Ya existe un acta de entrega");

        verify(negociacionService, never()).marcarEntregaRealizada(any(), any());
        verify(actaEntregaRepository, never()).save(any());
    }

    @Test
    void crearLanzaExcepcionCuandoLaNegociacionNoExiste() {
        UUID negociacionId = UUID.randomUUID();
        ActaEntregaRequest request = new ActaEntregaRequest();
        request.setNegociacionId(negociacionId);

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actaEntregaService.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class);

        verify(actaEntregaRepository, never()).save(any());
    }

    @Test
    void buscarPorNegociacionLanzaExcepcionCuandoNoHayActa() {
        UUID negociacionId = UUID.randomUUID();
        when(actaEntregaRepository.findByNegociacionId(negociacionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> actaEntregaService.buscarPorNegociacion(negociacionId))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
