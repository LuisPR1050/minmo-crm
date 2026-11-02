package com.felipeparodi.crmbackend.cucumber;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.captacion.entity.EstadoCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.EstadoNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
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
import com.felipeparodi.crmbackend.negociacion.service.NegociacionService;
import com.felipeparodi.crmbackend.negociacion.util.GeneradorNumeroNegocio;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class NegociacionCierreAutomaticoSteps {

    private NegociacionRepository negociacionRepository;
    private InmuebleRepository inmuebleRepository;
    private NegociacionService negociacionService;

    private Negociacion negociacion;
    private UUID negociacionId;
    private NegociacionResponse resultado;

    @Before
    public void configurarMocks() {
        negociacionRepository = mock(NegociacionRepository.class);
        inmuebleRepository = mock(InmuebleRepository.class);
        ClienteRepository clienteRepository = mock(ClienteRepository.class);
        PropietarioRepository propietarioRepository = mock(PropietarioRepository.class);
        GeneradorNumeroNegocio generadorNumeroNegocio = mock(GeneradorNumeroNegocio.class);
        HistorialNegociacionRepository historialNegociacionRepository = mock(HistorialNegociacionRepository.class);
        ParteNegociacionRepository parteNegociacionRepository = mock(ParteNegociacionRepository.class);
        BienAsociadoRepository bienAsociadoRepository = mock(BienAsociadoRepository.class);
        DistribucionGastosRepository distribucionGastosRepository = mock(DistribucionGastosRepository.class);
        NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository =
                mock(NegociacionCondicionEspecialRepository.class);
        NegociacionItemEntregaRepository negociacionItemEntregaRepository = mock(NegociacionItemEntregaRepository.class);
        PlanDePagoRepository planDePagoRepository = mock(PlanDePagoRepository.class);
        DocumentoNegociacionRepository documentoNegociacionRepository = mock(DocumentoNegociacionRepository.class);
        ActaEntregaRepository actaEntregaRepository = mock(ActaEntregaRepository.class);
        PromesaCompraventaRepository promesaCompraventaRepository = mock(PromesaCompraventaRepository.class);
        ReciboPagoRepository reciboPagoRepository = mock(ReciboPagoRepository.class);
        AlmacenamientoArchivosService almacenamientoArchivosService = mock(AlmacenamientoArchivosService.class);

        negociacionService = new NegociacionService(negociacionRepository, inmuebleRepository, clienteRepository,
                propietarioRepository, generadorNumeroNegocio, historialNegociacionRepository,
                parteNegociacionRepository, bienAsociadoRepository, distribucionGastosRepository,
                negociacionCondicionEspecialRepository, negociacionItemEntregaRepository, planDePagoRepository,
                documentoNegociacionRepository, actaEntregaRepository, promesaCompraventaRepository,
                reciboPagoRepository, almacenamientoArchivosService);

        when(negociacionRepository.save(any(Negociacion.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Negociacion construirNegociacion(EstadoNegociacion estado, boolean escrituraFirmada,
                                              boolean pagoRecibido, boolean entregaRealizada) {
        Inmueble inmueble = Inmueble.builder()
                .id(UUID.randomUUID())
                .codigo("APT-000001")
                .estadoCaptacion(EstadoCaptacion.EN_NEGOCIACION)
                .build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).nombre("Cliente de prueba").build();
        Propietario propietario = Propietario.builder().id(UUID.randomUUID()).nombre("Propietario de prueba").build();

        negociacionId = UUID.randomUUID();
        return Negociacion.builder()
                .id(negociacionId)
                .numeroNegocio("NEG-000001")
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .estado(estado)
                .escrituraFirmada(escrituraFirmada)
                .pagoRecibido(pagoRecibido)
                .entregaRealizada(entregaRealizada)
                .build();
    }

    @Dado("una negociación en estado {string} con escritura firmada y pago recibido")
    public void unaNegociacionConEscrituraYPago(String estado) {
        negociacion = construirNegociacion(EstadoNegociacion.valueOf(estado), true, true, false);
        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
    }

    @Dado("una negociación en estado {string} sin ningun hito cumplido")
    public void unaNegociacionSinHitos(String estado) {
        negociacion = construirNegociacion(EstadoNegociacion.valueOf(estado), false, false, false);
        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
    }

    @Dado("una negociación en estado {string} con los tres hitos cumplidos")
    public void unaNegociacionYaCerrada(String estado) {
        negociacion = construirNegociacion(EstadoNegociacion.valueOf(estado), true, true, true);
        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
    }

    @Cuando("se marca la entrega como realizada")
    public void seMarcaLaEntregaComoRealizada() {
        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);
        resultado = negociacionService.marcarEntregaRealizada(negociacionId, request);
    }

    @Cuando("se marca la escritura como firmada")
    public void seMarcaLaEscrituraComoFirmada() {
        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);
        resultado = negociacionService.marcarEscrituraFirmada(negociacionId, request);
    }

    @Cuando("se marca el pago como recibido nuevamente")
    public void seMarcaElPagoComoRecibidoNuevamente() {
        MarcarHitoRequest request = new MarcarHitoRequest();
        request.setValor(true);
        resultado = negociacionService.marcarPagoRecibido(negociacionId, request);
    }

    @Entonces("la negociación queda en estado {string}")
    public void laNegociacionQuedaEnEstado(String estadoEsperado) {
        assertThat(resultado.getEstado()).isEqualTo(estadoEsperado);
    }

    @Entonces("la negociación permanece en estado {string}")
    public void laNegociacionPermaneceEnEstado(String estadoEsperado) {
        assertThat(resultado.getEstado()).isEqualTo(estadoEsperado);
    }

    @Entonces("el inmueble queda marcado con estado de captación {string}")
    public void elInmuebleQuedaMarcadoConEstadoDeCaptacion(String estadoEsperado) {
        assertThat(negociacion.getInmueble().getEstadoCaptacion()).isEqualTo(EstadoCaptacion.valueOf(estadoEsperado));
    }

    @Entonces("el inmueble no se marca como vendido")
    public void elInmuebleNoSeMarcaComoVendido() {
        assertThat(negociacion.getInmueble().getEstadoCaptacion()).isNotEqualTo(EstadoCaptacion.VENDIDO);
    }

    @Entonces("el inmueble no se marca como vendido otra vez")
    public void elInmuebleNoSeMarcaComoVendidoOtraVez() {
        org.mockito.Mockito.verify(inmuebleRepository, org.mockito.Mockito.never()).save(any());
    }
}
