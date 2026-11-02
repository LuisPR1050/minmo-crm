package com.felipeparodi.crmbackend.negociacion.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.captacion.entity.EstadoCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.negociacion.dto.ActualizarCondicionesRequest;
import com.felipeparodi.crmbackend.negociacion.dto.CambiarEstadoNegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionRequest;
import com.felipeparodi.crmbackend.negociacion.dto.NegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.EstadoFisicoEntrega;
import com.felipeparodi.crmbackend.negociacion.entity.EstadoNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.MomentoEntrega;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.negociacion.repository.BienAsociadoRepository;
import com.felipeparodi.crmbackend.negociacion.repository.DistribucionGastosRepository;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionCondicionEspecialRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionItemEntregaRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.DocumentoNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import com.felipeparodi.crmbackend.negociacion.util.GeneradorNumeroNegocio;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class NegociacionService {

    private final NegociacionRepository negociacionRepository;
    private final InmuebleRepository inmuebleRepository;
    private final ClienteRepository clienteRepository;
    private final PropietarioRepository propietarioRepository;
    private final GeneradorNumeroNegocio generadorNumeroNegocio;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final ParteNegociacionRepository parteNegociacionRepository;
    private final BienAsociadoRepository bienAsociadoRepository;
    private final DistribucionGastosRepository distribucionGastosRepository;
    private final NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository;
    private final NegociacionItemEntregaRepository negociacionItemEntregaRepository;
    private final PlanDePagoRepository planDePagoRepository;
    private final DocumentoNegociacionRepository documentoNegociacionRepository;
    private final ActaEntregaRepository actaEntregaRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final ReciboPagoRepository reciboPagoRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public NegociacionService(NegociacionRepository negociacionRepository,
                               InmuebleRepository inmuebleRepository,
                               ClienteRepository clienteRepository,
                               PropietarioRepository propietarioRepository,
                               GeneradorNumeroNegocio generadorNumeroNegocio,
                               HistorialNegociacionRepository historialNegociacionRepository,
                               ParteNegociacionRepository parteNegociacionRepository,
                               BienAsociadoRepository bienAsociadoRepository,
                               DistribucionGastosRepository distribucionGastosRepository,
                               NegociacionCondicionEspecialRepository negociacionCondicionEspecialRepository,
                               NegociacionItemEntregaRepository negociacionItemEntregaRepository,
                               PlanDePagoRepository planDePagoRepository,
                               DocumentoNegociacionRepository documentoNegociacionRepository,
                               ActaEntregaRepository actaEntregaRepository,
                               PromesaCompraventaRepository promesaCompraventaRepository,
                               ReciboPagoRepository reciboPagoRepository,
                               AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.negociacionRepository = negociacionRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.clienteRepository = clienteRepository;
        this.propietarioRepository = propietarioRepository;
        this.generadorNumeroNegocio = generadorNumeroNegocio;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.bienAsociadoRepository = bienAsociadoRepository;
        this.distribucionGastosRepository = distribucionGastosRepository;
        this.negociacionCondicionEspecialRepository = negociacionCondicionEspecialRepository;
        this.negociacionItemEntregaRepository = negociacionItemEntregaRepository;
        this.planDePagoRepository = planDePagoRepository;
        this.documentoNegociacionRepository = documentoNegociacionRepository;
        this.actaEntregaRepository = actaEntregaRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.reciboPagoRepository = reciboPagoRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public NegociacionResponse crear(NegociacionRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id: " + request.getClienteId()));

        Propietario propietario = propietarioRepository.findById(request.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Propietario no encontrado con id: " + request.getPropietarioId()));

        Negociacion negociacion = Negociacion.builder()
                .numeroNegocio(generadorNumeroNegocio.generar())
                .inmueble(inmueble)
                .cliente(cliente)
                .propietario(propietario)
                .asesorId(request.getAsesorId())
                .fechaInicio(request.getFechaInicio())
                .precioInicialPublicado(request.getPrecioInicialPublicado())
                .ofertaInicialComprador(request.getOfertaInicialComprador())
                .build();

        Negociacion guardada = negociacionRepository.save(negociacion);

        registrarHistorial(guardada, "Negociacion iniciada");

        return mapearAResponse(guardada);
    }

    public List<NegociacionResponse> listarTodos() {
        return negociacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public NegociacionResponse buscarPorId(UUID id) {
        Negociacion negociacion = negociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + id));

        return mapearAResponse(negociacion);
    }

    public void eliminar(UUID id) {
        Negociacion negociacion = negociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + id));

        if (negociacion.getEstado() == EstadoNegociacion.NEGOCIO_CERRADO) {
            throw new IllegalArgumentException("No se puede eliminar una negociacion con el negocio ya cerrado");
        }
        if (actaEntregaRepository.findByNegociacionId(id).isPresent()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la negociacion porque tiene un acta de entrega generada");
        }
        if (promesaCompraventaRepository.findByNegociacionId(id).isPresent()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la negociacion porque tiene una promesa de compraventa generada");
        }
        if (!reciboPagoRepository.findByNegociacionId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar la negociacion porque tiene recibos de pago registrados");
        }

        var documentos = documentoNegociacionRepository.findByNegociacionId(id);
        documentos.forEach(documento -> almacenamientoArchivosService.eliminarPorRutaPublica(documento.getUrl()));
        documentoNegociacionRepository.deleteAll(documentos);
        parteNegociacionRepository.deleteAll(parteNegociacionRepository.findByNegociacionId(id));
        bienAsociadoRepository.deleteAll(bienAsociadoRepository.findByNegociacionId(id));
        distribucionGastosRepository.findByNegociacionId(id).ifPresent(distribucionGastosRepository::delete);
        negociacionCondicionEspecialRepository.deleteAll(negociacionCondicionEspecialRepository.findByNegociacionId(id));
        negociacionItemEntregaRepository.deleteAll(negociacionItemEntregaRepository.findByNegociacionId(id));
        planDePagoRepository.deleteAll(planDePagoRepository.findByNegociacionId(id));
        historialNegociacionRepository.deleteAll(historialNegociacionRepository.findByNegociacionId(id));

        negociacionRepository.delete(negociacion);
    }

    public NegociacionResponse actualizarCondiciones(UUID id, ActualizarCondicionesRequest request) {
        Negociacion negociacion = negociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + id));

        if (request.getContraofertaVendedor() != null) {
            negociacion.setContraofertaVendedor(request.getContraofertaVendedor());
        }
        if (request.getPrecioFinalAcordado() != null) {
            negociacion.setPrecioFinalAcordado(request.getPrecioFinalAcordado());
        }
        if (request.getFechaFirmaPcv() != null) {
            negociacion.setFechaFirmaPcv(request.getFechaFirmaPcv());
        }
        if (request.getFechaEntregaProgramada() != null) {
            negociacion.setFechaEntregaProgramada(request.getFechaEntregaProgramada());
        }
        if (request.getEntidadFinanciera() != null) {
            negociacion.setEntidadFinanciera(request.getEntidadFinanciera());
        }
        if (request.getFinanciacionDirectaCondiciones() != null) {
            negociacion.setFinanciacionDirectaCondiciones(request.getFinanciacionDirectaCondiciones());
        }
        if (request.getPermutaCondiciones() != null) {
            negociacion.setPermutaCondiciones(request.getPermutaCondiciones());
        }
        if (request.getEstadoFisicoEntrega() != null) {
            negociacion.setEstadoFisicoEntrega(EstadoFisicoEntrega.valueOf(request.getEstadoFisicoEntrega()));
        }
        if (request.getMomentoEntrega() != null) {
            negociacion.setMomentoEntrega(MomentoEntrega.valueOf(request.getMomentoEntrega()));
        }

        Negociacion actualizada = negociacionRepository.save(negociacion);

        return mapearAResponse(actualizada);
    }

    public NegociacionResponse cambiarEstado(UUID id, CambiarEstadoNegociacionRequest request) {
        Negociacion negociacion = negociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + id));

        if (negociacion.getEstado() == EstadoNegociacion.NEGOCIO_CERRADO) {
            throw new IllegalArgumentException("No se puede cambiar el estado de un negocio ya cerrado");
        }

        EstadoNegociacion nuevoEstado = EstadoNegociacion.valueOf(request.getNuevoEstado());
        if (nuevoEstado == EstadoNegociacion.NEGOCIO_CERRADO) {
            throw new IllegalArgumentException(
                    "El negocio se cierra automaticamente cuando se cumplen los 3 hitos "
                            + "(escritura firmada, pago recibido y entrega realizada)");
        }
        negociacion.setEstado(nuevoEstado);

        Negociacion actualizada = negociacionRepository.save(negociacion);

        registrarHistorial(actualizada, "Estado cambiado a " + nuevoEstado.name());

        return mapearAResponse(actualizada);
    }

    public NegociacionResponse marcarEscrituraFirmada(UUID id, MarcarHitoRequest request) {
        Negociacion negociacion = obtenerNegociacion(id);
        negociacion.setEscrituraFirmada(request.isValor());
        return guardarYRevisarCierre(negociacion, "Escritura firmada: " + request.isValor());
    }

    public NegociacionResponse marcarPagoRecibido(UUID id, MarcarHitoRequest request) {
        Negociacion negociacion = obtenerNegociacion(id);
        negociacion.setPagoRecibido(request.isValor());
        return guardarYRevisarCierre(negociacion, "Pago recibido: " + request.isValor());
    }

    public NegociacionResponse marcarEntregaRealizada(UUID id, MarcarHitoRequest request) {
        Negociacion negociacion = obtenerNegociacion(id);
        negociacion.setEntregaRealizada(request.isValor());
        return guardarYRevisarCierre(negociacion, "Entrega realizada: " + request.isValor());
    }

    private Negociacion obtenerNegociacion(UUID id) {
        return negociacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + id));
    }

    private NegociacionResponse guardarYRevisarCierre(Negociacion negociacion, String evento) {
        Negociacion guardada = negociacionRepository.save(negociacion);
        registrarHistorial(guardada, evento);

        if (guardada.isEscrituraFirmada() && guardada.isPagoRecibido() && guardada.isEntregaRealizada()
                && guardada.getEstado() != EstadoNegociacion.NEGOCIO_CERRADO) {

            guardada.setEstado(EstadoNegociacion.NEGOCIO_CERRADO);
            guardada = negociacionRepository.save(guardada);

            Inmueble inmueble = guardada.getInmueble();
            inmueble.setEstadoCaptacion(EstadoCaptacion.VENDIDO);
            inmuebleRepository.save(inmueble);

            registrarHistorial(guardada, "Cierre automatico: negocio cerrado, inmueble marcado como vendido");
        }

        return mapearAResponse(guardada);
    }

    private void registrarHistorial(Negociacion negociacion, String evento) {
        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private NegociacionResponse mapearAResponse(Negociacion negociacion) {
        return NegociacionResponse.builder()
                .id(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .inmuebleId(negociacion.getInmueble().getId())
                .inmuebleCodigo(negociacion.getInmueble().getCodigo())
                .clienteId(negociacion.getCliente().getId())
                .clienteNombre(negociacion.getCliente().getNombre())
                .propietarioId(negociacion.getPropietario().getId())
                .propietarioNombre(negociacion.getPropietario().getNombre())
                .asesorId(negociacion.getAsesorId())
                .fechaInicio(negociacion.getFechaInicio())
                .fechaFirmaPcv(negociacion.getFechaFirmaPcv())
                .fechaEntregaProgramada(negociacion.getFechaEntregaProgramada())
                .precioInicialPublicado(negociacion.getPrecioInicialPublicado())
                .ofertaInicialComprador(negociacion.getOfertaInicialComprador())
                .contraofertaVendedor(negociacion.getContraofertaVendedor())
                .precioFinalAcordado(negociacion.getPrecioFinalAcordado())
                .entidadFinanciera(negociacion.getEntidadFinanciera())
                .financiacionDirectaCondiciones(negociacion.getFinanciacionDirectaCondiciones())
                .permutaCondiciones(negociacion.getPermutaCondiciones())
                .estadoFisicoEntrega(negociacion.getEstadoFisicoEntrega() != null
                        ? negociacion.getEstadoFisicoEntrega().name() : null)
                .momentoEntrega(negociacion.getMomentoEntrega() != null
                        ? negociacion.getMomentoEntrega().name() : null)
                .escrituraFirmada(negociacion.isEscrituraFirmada())
                .pagoRecibido(negociacion.isPagoRecibido())
                .entregaRealizada(negociacion.isEntregaRealizada())
                .estado(negociacion.getEstado().name())
                .createdAt(negociacion.getCreatedAt())
                .build();
    }
}