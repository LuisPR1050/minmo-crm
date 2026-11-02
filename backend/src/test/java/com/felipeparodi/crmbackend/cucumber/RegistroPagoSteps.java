package com.felipeparodi.crmbackend.cucumber;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.documentacion.service.ReciboPagoService;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroReciboPago;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class RegistroPagoSteps {

    private NegociacionRepository negociacionRepository;
    private ReciboPagoRepository reciboPagoRepository;
    private ReciboPagoService reciboPagoService;

    private Negociacion negociacion;
    private UUID negociacionId;
    private ReciboPagoResponse resultado;

    @Before
    public void configurarMocks() {
        reciboPagoRepository = mock(ReciboPagoRepository.class);
        negociacionRepository = mock(NegociacionRepository.class);
        PromesaCompraventaRepository promesaCompraventaRepository = mock(PromesaCompraventaRepository.class);
        PlanDePagoRepository planDePagoRepository = mock(PlanDePagoRepository.class);
        ParteNegociacionRepository parteNegociacionRepository = mock(ParteNegociacionRepository.class);
        HistorialNegociacionRepository historialNegociacionRepository = mock(HistorialNegociacionRepository.class);
        GeneradorNumeroReciboPago generadorNumeroReciboPago = mock(GeneradorNumeroReciboPago.class);

        reciboPagoService = new ReciboPagoService(reciboPagoRepository, negociacionRepository,
                promesaCompraventaRepository, planDePagoRepository, parteNegociacionRepository,
                historialNegociacionRepository, generadorNumeroReciboPago);

        when(generadorNumeroReciboPago.generar()).thenReturn("REC-000001");
        when(parteNegociacionRepository.findByNegociacionId(any())).thenReturn(Collections.emptyList());
        when(reciboPagoRepository.save(any(ReciboPago.class))).thenAnswer(inv -> {
            ReciboPago recibo = inv.getArgument(0);
            recibo.setId(UUID.randomUUID());
            return recibo;
        });
    }

    private Negociacion construirNegociacion(BigDecimal precioFinalAcordado) {
        Inmueble inmueble = Inmueble.builder().id(UUID.randomUUID()).codigo("APT-000001").build();
        Cliente cliente = Cliente.builder().id(UUID.randomUUID()).nombre("Comprador").build();
        Propietario propietario = Propietario.builder().id(UUID.randomUUID()).nombre("Vendedor").build();

        negociacionId = UUID.randomUUID();
        return Negociacion.builder()
                .id(negociacionId)
                .numeroNegocio("NEG-000001")
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .precioFinalAcordado(precioFinalAcordado)
                .build();
    }

    @Dado("una negociación con precio final acordado de {long} y pagos previos por {long}")
    public void unaNegociacionConPrecioFinalYPagosPrevios(long precioFinalAcordado, long pagosPrevios) {
        negociacion = construirNegociacion(BigDecimal.valueOf(precioFinalAcordado));
        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
        when(reciboPagoRepository.sumarValorRecibidoPorNegociacion(negociacionId))
                .thenReturn(BigDecimal.valueOf(pagosPrevios));
    }

    @Dado("una negociación sin precio final acordado")
    public void unaNegociacionSinPrecioFinalAcordado() {
        negociacion = construirNegociacion(null);
        when(negociacionRepository.findById(negociacionId)).thenReturn(Optional.of(negociacion));
    }

    @Cuando("se registra un recibo de pago por {long}")
    public void seRegistraUnReciboDePagoPor(long valorRecibido) {
        ReciboPagoRequest request = new ReciboPagoRequest();
        request.setNegociacionId(negociacionId);
        request.setConceptoPago("CUOTA_INICIAL");
        request.setValorRecibido(BigDecimal.valueOf(valorRecibido));

        resultado = reciboPagoService.crear(request);
    }

    @Entonces("el saldo pendiente del recibo es {long}")
    public void elSaldoPendienteDelReciboEs(long saldoEsperado) {
        assertThat(resultado.getSaldoPendiente()).isEqualByComparingTo(BigDecimal.valueOf(saldoEsperado));
    }

    @Entonces("el saldo pendiente del recibo es nulo")
    public void elSaldoPendienteDelReciboEsNulo() {
        assertThat(resultado.getSaldoPendiente()).isNull();
    }
}
