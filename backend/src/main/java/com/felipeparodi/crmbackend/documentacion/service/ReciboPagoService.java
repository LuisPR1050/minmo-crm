package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ReciboPagoResponse;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenPagosNegociacionResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ConceptoPago;
import com.felipeparodi.crmbackend.documentacion.entity.FormaPagoRecibo;
import com.felipeparodi.crmbackend.documentacion.entity.PagoRealizadoPor;
import com.felipeparodi.crmbackend.documentacion.entity.PagoRecibidoPor;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroReciboPago;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
import com.felipeparodi.crmbackend.negociacion.entity.RolParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ReciboPagoService {

    private final ReciboPagoRepository reciboPagoRepository;
    private final NegociacionRepository negociacionRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final PlanDePagoRepository planDePagoRepository;
    private final ParteNegociacionRepository parteNegociacionRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final GeneradorNumeroReciboPago generadorNumeroReciboPago;

    public ReciboPagoService(ReciboPagoRepository reciboPagoRepository,
                              NegociacionRepository negociacionRepository,
                              PromesaCompraventaRepository promesaCompraventaRepository,
                              PlanDePagoRepository planDePagoRepository,
                              ParteNegociacionRepository parteNegociacionRepository,
                              HistorialNegociacionRepository historialNegociacionRepository,
                              GeneradorNumeroReciboPago generadorNumeroReciboPago) {
        this.reciboPagoRepository = reciboPagoRepository;
        this.negociacionRepository = negociacionRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.planDePagoRepository = planDePagoRepository;
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.generadorNumeroReciboPago = generadorNumeroReciboPago;
    }

    public ReciboPagoResponse crear(ReciboPagoRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        PromesaCompraventa promesa = null;
        if (request.getPromesaCompraventaId() != null) {
            promesa = promesaCompraventaRepository.findById(request.getPromesaCompraventaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Promesa de compraventa no encontrada con id: " + request.getPromesaCompraventaId()));
        }

        PlanDePago plan = null;
        if (request.getPlanDePagoId() != null) {
            plan = planDePagoRepository.findById(request.getPlanDePagoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Plan de pago no encontrado con id: " + request.getPlanDePagoId()));
        }

        if (request.getConceptoPago() == null) {
            throw new IllegalArgumentException("El concepto de pago es obligatorio");
        }
        if (request.getValorRecibido() == null) {
            throw new IllegalArgumentException("El valor recibido es obligatorio");
        }

        BigDecimal saldoPendiente = calcularSaldoPendiente(negociacion, request.getValorRecibido());

        ReciboPago recibo = ReciboPago.builder()
                .numeroRecibo(generadorNumeroReciboPago.generar())
                .negociacion(negociacion)
                .promesaCompraventa(promesa)
                .planDePago(plan)
                .fechaElaboracion(request.getFechaElaboracion())
                .conceptoPago(ConceptoPago.valueOf(request.getConceptoPago()))
                .numeroCuota(request.getNumeroCuota())
                .valorRecibido(request.getValorRecibido())
                .saldoPendiente(saldoPendiente)
                .moneda(request.getMoneda())
                .formaPago(request.getFormaPago() != null ? FormaPagoRecibo.valueOf(request.getFormaPago()) : null)
                .pagoRealizadoPor(request.getPagoRealizadoPor() != null
                        ? PagoRealizadoPor.valueOf(request.getPagoRealizadoPor()) : null)
                .pagoRecibidoPor(request.getPagoRecibidoPor() != null
                        ? PagoRecibidoPor.valueOf(request.getPagoRecibidoPor()) : null)
                .observaciones(request.getObservaciones())
                .build();

        ReciboPago guardado = reciboPagoRepository.save(recibo);

        if (plan != null) {
            plan.setPagada(true);
            planDePagoRepository.save(plan);
        }

        registrarHistorialNegocio(negociacion, "Recibo de pago registrado: " + guardado.getNumeroRecibo()
                + " (" + guardado.getConceptoPago().name() + ", " + guardado.getValorRecibido() + ")"
                + (saldoPendiente != null ? " - saldo pendiente: " + saldoPendiente : ""));

        return mapearAResponse(guardado);
    }

    public List<ReciboPagoResponse> listarPorNegociacion(UUID negociacionId) {
        return reciboPagoRepository.findByNegociacionId(negociacionId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ReciboPagoResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerRecibo(id));
    }

    public ResumenPagosNegociacionResponse resumenPagos(UUID negociacionId) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + negociacionId));

        BigDecimal totalPagado = reciboPagoRepository.sumarValorRecibidoPorNegociacion(negociacionId);
        BigDecimal totalPactado = negociacion.getPrecioFinalAcordado();
        BigDecimal saldoPendiente = totalPactado != null ? totalPactado.subtract(totalPagado) : null;
        long cantidadRecibos = reciboPagoRepository.findByNegociacionId(negociacionId).size();

        return ResumenPagosNegociacionResponse.builder()
                .negociacionId(negociacionId)
                .totalPactado(totalPactado)
                .totalPagado(totalPagado)
                .saldoPendiente(saldoPendiente)
                .cantidadRecibos(cantidadRecibos)
                .build();
    }

    private BigDecimal calcularSaldoPendiente(Negociacion negociacion, BigDecimal valorNuevoPago) {
        if (negociacion.getPrecioFinalAcordado() == null) {
            return null;
        }

        BigDecimal totalPagadoPrevio = reciboPagoRepository.sumarValorRecibidoPorNegociacion(negociacion.getId());
        BigDecimal totalPagado = totalPagadoPrevio.add(valorNuevoPago);

        return negociacion.getPrecioFinalAcordado().subtract(totalPagado);
    }

    private ReciboPago obtenerRecibo(UUID id) {
        return reciboPagoRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Recibo de pago no encontrado con id: " + id));
    }

    private void registrarHistorialNegocio(Negociacion negociacion, String evento) {
        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private ReciboPagoResponse mapearAResponse(ReciboPago recibo) {
        Negociacion negociacion = recibo.getNegociacion();
        List<ParteNegociacion> partes = parteNegociacionRepository.findByNegociacionId(negociacion.getId());

        Optional<ParteNegociacion> parteComprador = partes.stream()
                .filter(p -> p.getRol() == RolParteNegociacion.COMPRADOR)
                .findFirst();
        Optional<ParteNegociacion> parteVendedor = partes.stream()
                .filter(p -> p.getRol() == RolParteNegociacion.VENDEDOR)
                .findFirst();

        String compradorDocumento = parteComprador
                .map(p -> (p.getTipoDocumento() != null ? p.getTipoDocumento() + " " : "") + p.getNumeroDocumento())
                .orElse(null);
        String vendedorDocumento = parteVendedor
                .map(p -> (p.getTipoDocumento() != null ? p.getTipoDocumento() + " " : "") + p.getNumeroDocumento())
                .orElse(null);

        return ReciboPagoResponse.builder()
                .id(recibo.getId())
                .numeroRecibo(recibo.getNumeroRecibo())
                .negociacionId(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .promesaCompraventaId(recibo.getPromesaCompraventa() != null ? recibo.getPromesaCompraventa().getId() : null)
                .planDePagoId(recibo.getPlanDePago() != null ? recibo.getPlanDePago().getId() : null)
                .fechaElaboracion(recibo.getFechaElaboracion())
                .conceptoPago(recibo.getConceptoPago().name())
                .numeroCuota(recibo.getNumeroCuota())
                .valorRecibido(recibo.getValorRecibido())
                .saldoPendiente(recibo.getSaldoPendiente())
                .moneda(recibo.getMoneda())
                .formaPago(recibo.getFormaPago() != null ? recibo.getFormaPago().name() : null)
                .pagoRealizadoPor(recibo.getPagoRealizadoPor() != null ? recibo.getPagoRealizadoPor().name() : null)
                .pagoRecibidoPor(recibo.getPagoRecibidoPor() != null ? recibo.getPagoRecibidoPor().name() : null)
                .observaciones(recibo.getObservaciones())
                .inmuebleId(negociacion.getInmueble().getId())
                .inmuebleCodigo(negociacion.getInmueble().getCodigo())
                .compradorNombre(negociacion.getCliente().getNombre())
                .compradorDocumento(compradorDocumento)
                .compradorTelefono(negociacion.getCliente().getTelefono())
                .compradorCorreo(negociacion.getCliente().getCorreoElectronico())
                .vendedorNombre(negociacion.getPropietario().getNombre())
                .vendedorDocumento(vendedorDocumento)
                .vendedorTelefono(negociacion.getPropietario().getTelefonoPrincipal())
                .vendedorCorreo(negociacion.getPropietario().getCorreoElectronico())
                .createdAt(recibo.getCreatedAt())
                .build();
    }
}
