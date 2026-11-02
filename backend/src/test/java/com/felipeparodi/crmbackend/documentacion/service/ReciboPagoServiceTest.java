package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroReciboPago;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ReciboPagoServiceTest {

    @Mock
    private ReciboPagoRepository reciboPagoRepository;
    @Mock
    private NegociacionRepository negociacionRepository;
    @Mock
    private PromesaCompraventaRepository promesaCompraventaRepository;
    @Mock
    private PlanDePagoRepository planDePagoRepository;
    @Mock
    private ParteNegociacionRepository parteNegociacionRepository;
    @Mock
    private HistorialNegociacionRepository historialNegociacionRepository;
    @Mock
    private GeneradorNumeroReciboPago generadorNumeroReciboPago;

    private ReciboPagoService reciboPagoService;

    @BeforeEach
    void setUp() {
        reciboPagoService = new ReciboPagoService(reciboPagoRepository, negociacionRepository,
                promesaCompraventaRepository, planDePagoRepository, parteNegociacionRepository,
                historialNegociacionRepository, generadorNumeroReciboPago);
    }

    private Negociacion negociacionConPrecio(BigDecimal precioFinalAcordado) {
        Inmueble inmueble = Inmueble.builder().id(UUID.randomUUID()).codigo("APT-000001").build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).nombre("Comprador").build();
        Propietario propietario = Propietario.builder().id(UUID.randomUUID()).nombre("Vendedor").build();

        return Negociacion.builder()
                .id(UUID.randomUUID())
                .numeroNegocio("NEG-000001")
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .precioFinalAcordado(precioFinalAcordado)
                .build();
    }

    @Test
    void calculaElSaldoPendienteRestandoLosPagosPreviosYElNuevoPago() {
        Negociacion negociacion = negociacionConPrecio(new BigDecimal("100000000"));
        UUID negociacionId = negociacion.getId();

        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);
        request.setConceptoPago("CUOTA_INICIAL");
        request.setValorRecibido(new BigDecimal("20000000"));

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
        when(reciboPagoRepository.sumarValorRecibidoPorNegociacion(negociacionId)).thenReturn(new BigDecimal("30000000"));
        when(generadorNumeroReciboPago.generar()).thenReturn("REC-000001");
        when(reciboPagoRepository.save(any(ReciboPago.class))).thenAnswer(inv -> {
            ReciboPago recibo = inv.getArgument(0);
            recibo.setId(UUID.randomUUID());
            return recibo;
        });
        when(parteNegociacionRepository.findByNegociacionId(negociacionId)).thenReturn(Collections.emptyList());

        ReciboPagoResponse response = reciboPagoService.crear(request);

        assertThat(response.getSaldoPendiente()).isEqualByComparingTo(new BigDecimal("50000000"));
    }

    @Test
    void elSaldoPendienteEsNuloCuandoNoHayPrecioFinalAcordado() {
        Negociacion negociacion = negociacionConPrecio(null);
        UUID negociacionId = negociacion.getId();

        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);
        request.setConceptoPago("CUOTA_INICIAL");
        request.setValorRecibido(new BigDecimal("20000000"));

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
        when(generadorNumeroReciboPago.generar()).thenReturn("REC-000002");
        when(reciboPagoRepository.save(any(ReciboPago.class))).thenAnswer(inv -> {
            ReciboPago recibo = inv.getArgument(0);
            recibo.setId(UUID.randomUUID());
            return recibo;
        });
        when(parteNegociacionRepository.findByNegociacionId(negociacionId)).thenReturn(Collections.emptyList());

        ReciboPagoResponse response = reciboPagoService.crear(request);

        assertThat(response.getSaldoPendiente()).isNull();
    }

    @Test
    void crearLanzaExcepcionCuandoElConceptoDePagoEsNulo() {
        Negociacion negociacion = negociacionConPrecio(new BigDecimal("100000000"));
        UUID negociacionId = negociacion.getId();

        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);
        request.setValorRecibido(new BigDecimal("20000000"));

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));

        assertThatThrownBy(() -> reciboPagoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("concepto de pago");
    }

    @Test
    void crearLanzaExcepcionCuandoElValorRecibidoEsNulo() {
        Negociacion negociacion = negociacionConPrecio(new BigDecimal("100000000"));
        UUID negociacionId = negociacion.getId();

        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);
        request.setConceptoPago("CUOTA_INICIAL");

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));

        assertThatThrownBy(() -> reciboPagoService.crear(request))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valor recibido");
    }

    @Test
    void crearLanzaExcepcionCuandoLaNegociacionNoExiste() {
        UUID negociacionId = UUID.randomUUID();
        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);

        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> reciboPagoService.crear(request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
