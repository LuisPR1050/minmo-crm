package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.DeclaracionComprador;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroActaEntrega;
import com.felipeparodi.crmbackend.negociacion.dto.MarcarHitoRequest;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.RolParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.service.NegociacionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
public class ActaEntregaService {

    private final ActaEntregaRepository actaEntregaRepository;
    private final NegociacionRepository negociacionRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final ParteNegociacionRepository parteNegociacionRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final GeneradorNumeroActaEntrega generadorNumeroActaEntrega;
    private final NegociacionService negociacionService;

    public ActaEntregaService(ActaEntregaRepository actaEntregaRepository,
                               NegociacionRepository negociacionRepository,
                               PromesaCompraventaRepository promesaCompraventaRepository,
                               ParteNegociacionRepository parteNegociacionRepository,
                               HistorialNegociacionRepository historialNegociacionRepository,
                               GeneradorNumeroActaEntrega generadorNumeroActaEntrega,
                               NegociacionService negociacionService) {
        this.actaEntregaRepository = actaEntregaRepository;
        this.negociacionRepository = negociacionRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.generadorNumeroActaEntrega = generadorNumeroActaEntrega;
        this.negociacionService = negociacionService;
    }

    public ActaEntregaResponse crear(ActaEntregaRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        if (actaEntregaRepository.findByNegociacionId(negociacion.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe un acta de entrega para esta negociacion");
        }

        PromesaCompraventa promesa = null;
        if (request.getPromesaCompraventaId() != null) {
            promesa = promesaCompraventaRepository.findById(request.getPromesaCompraventaId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Promesa de compraventa no encontrada con id: " + request.getPromesaCompraventaId()));
        }

        ActaEntrega acta = ActaEntrega.builder()
                .numeroActa(generadorNumeroActaEntrega.generar())
                .negociacion(negociacion)
                .promesaCompraventa(promesa)
                .fechaElaboracion(request.getFechaElaboracion())
                .fechaHoraEntrega(request.getFechaHoraEntrega())
                .ciudad(request.getCiudad())
                .lecturaEnergia(request.getLecturaEnergia())
                .lecturaAgua(request.getLecturaAgua())
                .lecturaGas(request.getLecturaGas())
                .danosExistentes(request.getDanosExistentes())
                .reparacionesPendientes(request.getReparacionesPendientes())
                .acuerdosPosteriores(request.getAcuerdosPosteriores())
                .compromisosAsumidos(request.getCompromisosAsumidos())
                .declaracionComprador(request.getDeclaracionComprador() != null
                        ? DeclaracionComprador.valueOf(request.getDeclaracionComprador()) : null)
                .vendedorEntregaConforme(request.isVendedorEntregaConforme())
                .observaciones(request.getObservaciones())
                .build();

        ActaEntrega guardada = actaEntregaRepository.save(acta);

        registrarHistorialNegocio(negociacion, "Acta de entrega generada: " + guardada.getNumeroActa());

        MarcarHitoRequest marcarEntrega = new MarcarHitoRequest();
        marcarEntrega.setValor(true);
        negociacionService.marcarEntregaRealizada(negociacion.getId(), marcarEntrega);

        return mapearAResponse(guardada);
    }

    public List<ActaEntregaResponse> listarTodas() {
        return actaEntregaRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ActaEntregaResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerActa(id));
    }

    public ActaEntregaResponse buscarPorNegociacion(UUID negociacionId) {
        return actaEntregaRepository.findByNegociacionId(negociacionId)
                .map(this::mapearAResponse)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe acta de entrega para la negociacion: " + negociacionId));
    }

    private ActaEntrega obtenerActa(UUID id) {
        return actaEntregaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + id));
    }

    private void registrarHistorialNegocio(Negociacion negociacion, String evento) {
        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private ActaEntregaResponse mapearAResponse(ActaEntrega acta) {
        Negociacion negociacion = acta.getNegociacion();
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

        return ActaEntregaResponse.builder()
                .id(acta.getId())
                .numeroActa(acta.getNumeroActa())
                .negociacionId(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .promesaCompraventaId(acta.getPromesaCompraventa() != null ? acta.getPromesaCompraventa().getId() : null)
                .fechaElaboracion(acta.getFechaElaboracion())
                .fechaHoraEntrega(acta.getFechaHoraEntrega())
                .ciudad(acta.getCiudad())
                .inmuebleId(negociacion.getInmueble().getId())
                .inmuebleCodigo(negociacion.getInmueble().getCodigo())
                .direccionExacta(negociacion.getInmueble().getDireccionExacta())
                .inmuebleCiudad(negociacion.getInmueble().getCiudad())
                .matriculaInmobiliaria(negociacion.getInmueble().getMatriculaInmobiliaria())
                .referenciaCatastral(negociacion.getInmueble().getReferenciaCatastral())
                .vendedorNombre(negociacion.getPropietario().getNombre())
                .vendedorDocumento(vendedorDocumento)
                .vendedorTelefono(negociacion.getPropietario().getTelefonoPrincipal())
                .compradorNombre(negociacion.getCliente().getNombre())
                .compradorDocumento(compradorDocumento)
                .compradorTelefono(negociacion.getCliente().getTelefono())
                .lecturaEnergia(acta.getLecturaEnergia())
                .lecturaAgua(acta.getLecturaAgua())
                .lecturaGas(acta.getLecturaGas())
                .danosExistentes(acta.getDanosExistentes())
                .reparacionesPendientes(acta.getReparacionesPendientes())
                .acuerdosPosteriores(acta.getAcuerdosPosteriores())
                .compromisosAsumidos(acta.getCompromisosAsumidos())
                .declaracionComprador(acta.getDeclaracionComprador() != null ? acta.getDeclaracionComprador().name() : null)
                .vendedorEntregaConforme(acta.isVendedorEntregaConforme())
                .observaciones(acta.getObservaciones())
                .createdAt(acta.getCreatedAt())
                .build();
    }
}
