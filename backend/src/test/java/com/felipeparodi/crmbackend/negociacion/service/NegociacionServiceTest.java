package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.captacion.entity.EstadoCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import com.felipeparodi.crmbackend.negociacion.dto.CambiarEstadoNegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.EstadoNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.negociacion.repository.BienAsociadoRepository;
import com.felipeparodi.crmbackend.negociacion.repository.DistribucionGastosRepository;
import com.felipeparodi.crmbackend.negociacion.repository.DocumentoNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionCondicionEspecialRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionItemEntregaRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import com.felipeparodi.crmbackend.negociacion.util.GeneradorNumeroNegocio;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NegociacionServiceTest {

    @Mock
    private NegociacionRepository negociacionRepository;
    @Mock
    private InmuebleRepository inmuebleRepository;
    @Mock
    private ClienteRepository clienteRepository;
    @Mock
    private PropietarioRepository propietarioRepository;
    @Mock
    private GeneradorNumeroNegocio generadorNumeroNegocio;
    @Mock
    private HistorialNegociacionRepository historialNegociacionRepository;
    @Mock
    private ParteNegociacionRepository parteNegociacionRepository;
    @Mock
    private BienAsociadoRepository bienAsociadoRepository;
    @Mock
    private DistribucionGastosRepository distribucionGastosRepository;
    @Mock
    private NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository;
    @Mock
    private NegociacionItemEntregaRepository negociacionItemEntregaRepository;
    @Mock
    private PlanDePagoRepository planDePagoRepository;
    @Mock
    private DocumentoNegociacionRepository documentoNegociacionRepository;
    @Mock
    private ActaEntregaRepository actaEntregaRepository;
    @Mock
    private PromesaCompraventaRepository promesaCompraventaRepository;
    @Mock
    private ReciboPagoRepository reciboPagoRepository;
    @Mock
    private AlmacenamientoArchivosService almacenamientoArchivosService;

    private NegociacionService negociacionService;

    @BeforeEach
    void setUp() {
        negociacionService = new NegociacionService(negociacionRepository, inmuebleRepository, clienteRepository,
                propietarioRepository, generadorNumeroNegocio, historialNegociacionRepository,
                parteNegociacionRepository, bienAsociadoRepository, distribucionGastosRepository,
                negociacionCondicionEspecialRepository, negociacionItemEntregaRepository, planDePagoRepository,
                documentoNegociacionRepository, actaEntregaRepository, promesaCompraventaRepository,
                reciboPagoRepository, almacenamientoArchivosService);
    }

    private Negociacion negociacionDePrueba(boolean escrituraFirmada, boolean pagoRecibido, boolean entregaRealizada,
                                             EstadoNegociacion estado) {
        Inmueble inmueble = Inmueble.builder()
                .id(UUID.randomUUID())
                .codigo("APT-000001")
                .estadoCaptacion(EstadoCaptacion.EN_NEGOCIACION)
                .build();

        Cliente cliente = Cliente.builder()
                .id(UUID.randomUUID())
                .nombre("Cliente de prueba")
                .build();

        Propietario propietario = Propietario.builder()
                .id(UUID.randomUUID())
                .nombre("Propietario de prueba")
                .build();

        return Negociacion.builder()
                .id(UUID.randomUUID())
                .numeroNegocio("NEG-000001")
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .escrituraFirmada(escrituraFirmada)
                .pagoRecibido(pagoRecibido)
                .entregaRealizada(entregaRealizada)
                .estado(estado)
                .build();
    }

    @Test
    void crearAsignaNumeroDeNegocioYRegistraHistorial() {
        UUID inmuebleId = UUID.randomUUID();
        UUID clienteId = UUID.randomUUID();
        UUID propietarioId = UUID.randomUUID();

        NegociacionRequest request = new NegociacionRequest();
        request.setInmuebleId(inmuebleId);
        request.setClienteId(clienteId);
        request.setPropietarioId(propietarioId);
        request.setPrecioInicialPublicado(new BigDecimal("100000000"));

        Inmueble inmueble = Inmueble.builder().id(inmuebleId).codigo("APT-000002").build();
        Cliente cliente = Cliente.builder().id(clienteId).nombre("Ana").build();
        Propietario propietario = Propietario.builder().id(propietarioId).nombre("Pedro").build();

        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.of(inmueble));
        when(clienteRepository.findById(clienteId)).thenReturn(Optional.of(cliente));
        when(propietarioRepository.findById(propietarioId)).thenReturn(Optional.of(propietario));
        when(generadorNumeroNegocio.generar()).thenReturn("NEG-000099");
        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> {
            Negociacion n = inv.getArgument(0);
            n.setId(UUID.randomUUID());
            n.setEstado(EstadoNegociacion.NEGOCIACION_INICIADA);
            return n;
        });

        NegociacionResponse response = negociacionService.crear(request);

        assertThat(response.getNumeroNegocio()).isEqualTo("NEG-000099");
        verify(historialNegociacionRepository).save(any());
    }

    @Test
    void crearLanzaExcepcionCuandoElInmuebleNoExiste() {
        UUID inmuebleId = UUID.randomUUID();
        NegociacionRequest request = new NegociacionRequest();
        request.setInmuebleId(inmuebleId);

        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> negociacionService.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class)
                .hasMessageContaining(inmuebleId.toString());
    }

    @Test
    void marcarUnSoloHitoNoCierraElNegocio() {
        Negociacion negociacion = negociacionDePrueba(false, false, false, EstadoNegociacion.NEGOCIACION_INICIADA);
        UUID id = negociacion.getId();

        when(negociacionRepository.findById(id)).thenReturn(Optional.of(negociacion));
        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> inv.getArgument(0));

        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);

        NegociacionResponse response = negociacionService.marcarEscrituraFirmada(id, request);

        assertThat(response.isEscrituraFirmada()).isTrue();
        assertThat(response.getEstado()).isEqualTo(EstadoNegociacion.NEGOCIACION_INICIADA.name());
        verify(inmuebleRepository, never()).save(any());
    }

    @Test
    void marcarElUltimoHitoPendienteCierraElNegocioYMarcaElInmuebleComoVendido() {
        Negociacion negociacion = negociacionDePrueba(true, true, false, EstadoNegociacion.ESPERANDO_ESCRITURA);
        UUID id = negociacion.getId();

        when(negociacionRepository.findById(id)).thenReturn(Optional.of(negociacion));
        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> inv.getArgument(0));

        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);

        NegociacionResponse response = negociacionService.marcarEntregaRealizada(id, request);

        assertThat(response.isEntregaRealizada()).isTrue();
        assertThat(response.getEstado()).isEqualTo(EstadoNegociacion.NEGOCIO_CERRADO.name());

        ArgumentCaptor<Inmueble> inmuebleCaptor = ArgumentCaptor.forClass(Inmueble.class);
        verify(inmuebleRepository).save(inmuebleCaptor.capture());
        assertThat(inmuebleCaptor.getValue().getEstadoCaptacion()).isEqualTo(EstadoCaptacion.VENDIDO);

        verify(historialNegociacionRepository, times(2)).save(any());
    }

    @Test
    void marcarHitoCuandoElNegocioYaEstaCerradoNoRepiteElCierre() {
        Negociacion negociacion = negociacionDePrueba(true, true, true, EstadoNegociacion.NEGOCIO_CERRADO);
        UUID id = negociacion.getId();

        when(negociacionRepository.findById(id)).thenReturn(Optional.of(negociacion));
        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> inv.getArgument(0));

        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);

        negociacionService.marcarPagoRecibido(id, request);

        verify(inmuebleRepository, never()).save(any());
        verify(negociacionRepository, times(1)).save(any());
        verify(historialNegociacionRepository, times(1)).save(any());
    }

    @Test
    void cambiarEstadoActualizaYRegistraHistorial() {
        Negociacion negociacion = negociacionDePrueba(false, false, false, EstadoNegociacion.NEGOCIACION_INICIADA);
        UUID id = negociacion.getId();

        when(negociacionRepository.findById(id)).thenReturn(Optional.of(negociacion));
        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> inv.getArgument(0));

        CambiarEstadoNegociacionRequest request = new CambiarEstadoNegociacionRequest();
        request.setNuevoEstado("NEGOCIO_PERDIDO");

        NegociacionResponse response = negociacionService.cambiarEstado(id, request);

        assertThat(response.getEstado()).isEqualTo("NEGOCIO_PERDIDO");
        verify(historialNegociacionRepository).save(any());
    }

    @Test
    void buscarPorIdLanzaExcepcionCuandoNoExiste() {
        UUID id = UUID.randomUUID();
        when(negociacionRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> negociacionService.buscarPorId(id))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
