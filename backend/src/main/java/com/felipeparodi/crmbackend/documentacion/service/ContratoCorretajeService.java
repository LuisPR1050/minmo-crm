package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import com.felipeparodi.crmbackend.documentacion.dto.ActualizarContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CambiarEstadoContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ContratoCorretajeRequest;
import com.felipeparodi.crmbackend.documentacion.dto.ContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenContratosCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.EstadoDocumento;
import com.felipeparodi.crmbackend.documentacion.entity.FormaPagoComision;
import com.felipeparodi.crmbackend.documentacion.entity.HistorialContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.OpcionFirma;
import com.felipeparodi.crmbackend.documentacion.entity.TipoContrato;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeAutorizacionRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.DocumentoContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.HistorialContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroContratoCorretaje;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ContratoCorretajeService {

    private static final Set<EstadoDocumento> ESTADOS_QUE_EXIGEN_CAMPOS_COMPLETOS = EnumSet.of(
            EstadoDocumento.EN_REVISION,
            EstadoDocumento.ENVIADO,
            EstadoDocumento.PENDIENTE_FIRMA,
            EstadoDocumento.FIRMADO,
            EstadoDocumento.VIGENTE);

    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final InmuebleRepository inmuebleRepository;
    private final PropietarioRepository propietarioRepository;
    private final GeneradorNumeroContratoCorretaje generadorNumeroContratoCorretaje;
    private final HistorialContratoCorretajeRepository historialContratoCorretajeRepository;
    private final DocumentoContratoCorretajeRepository documentoContratoCorretajeRepository;
    private final ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository;
    private final ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository;
    private final PdfDocumentoService pdfDocumentoService;

    private static final String SUBCARPETA_PDF = "contratos-corretaje-pdf";

    public ContratoCorretajeService(ContratoCorretajeRepository contratoCorretajeRepository,
                                     InmuebleRepository inmuebleRepository,
                                     PropietarioRepository propietarioRepository,
                                     GeneradorNumeroContratoCorretaje generadorNumeroContratoCorretaje,
                                     HistorialContratoCorretajeRepository historialContratoCorretajeRepository,
                                     DocumentoContratoCorretajeRepository documentoContratoCorretajeRepository,
                                     ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository,
                                     ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository,
                                     PdfDocumentoService pdfDocumentoService) {
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.propietarioRepository = propietarioRepository;
        this.generadorNumeroContratoCorretaje = generadorNumeroContratoCorretaje;
        this.historialContratoCorretajeRepository = historialContratoCorretajeRepository;
        this.documentoContratoCorretajeRepository = documentoContratoCorretajeRepository;
        this.contratoCorretajeClausulaRepository = contratoCorretajeClausulaRepository;
        this.contratoCorretajeAutorizacionRepository = contratoCorretajeAutorizacionRepository;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public ContratoCorretajeResponse crear(ContratoCorretajeRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        Propietario propietario = propietarioRepository.findById(request.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Propietario no encontrado con id: " + request.getPropietarioId()));

        ContratoCorretaje contrato = ContratoCorretaje.builder()
                .numeroContrato(generadorNumeroContratoCorretaje.generar())
                .inmueble(inmueble)
                .propietario(propietario)
                .fechaElaboracion(request.getFechaElaboracion())
                .ciudad(request.getCiudad())
                .tipoContrato(request.getTipoContrato() != null ? TipoContrato.valueOf(request.getTipoContrato()) : null)
                .precioAutorizadoVenta(request.getPrecioAutorizadoVenta())
                .precioMinimoAutorizado(request.getPrecioMinimoAutorizado())
                .porcentajeComision(request.getPorcentajeComision())
                .valorFijoComision(request.getValorFijoComision())
                .sobreprecioComision(request.getSobreprecioComision())
                .formaPagoComision(request.getFormaPagoComision() != null
                        ? FormaPagoComision.valueOf(request.getFormaPagoComision()) : null)
                .fechaVigenciaHasta(request.getFechaVigenciaHasta())
                .opcionFirma(request.getOpcionFirma() != null ? OpcionFirma.valueOf(request.getOpcionFirma()) : null)
                .build();

        ContratoCorretaje guardado = contratoCorretajeRepository.save(contrato);

        registrarHistorial(guardado, "Contrato de corretaje creado");

        return mapearAResponse(guardado);
    }

    public List<ContratoCorretajeResponse> listarTodos() {
        return contratoCorretajeRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<ContratoCorretajeResponse> listarPorInmueble(UUID inmuebleId) {
        return contratoCorretajeRepository.findByInmuebleIdOrderByCreatedAtDesc(inmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ContratoCorretajeResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerContrato(id));
    }

    public void eliminar(UUID id) {
        ContratoCorretaje contrato = obtenerContrato(id);

        if (!documentoContratoCorretajeRepository.findByContratoCorretajeId(id).isEmpty()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el contrato porque tiene documentos adjuntos. Elimina primero los documentos.");
        }

        historialContratoCorretajeRepository.deleteAll(historialContratoCorretajeRepository.findByContratoCorretajeId(id));
        contratoCorretajeClausulaRepository.deleteAll(contratoCorretajeClausulaRepository.findByContratoCorretajeId(id));
        contratoCorretajeAutorizacionRepository.deleteAll(contratoCorretajeAutorizacionRepository.findByContratoCorretajeId(id));
        contratoCorretajeRepository.delete(contrato);
    }

    public ContratoCorretajeResponse actualizar(UUID id, ActualizarContratoCorretajeRequest request) {
        ContratoCorretaje contrato = obtenerContrato(id);

        if (request.getCiudad() != null) {
            contrato.setCiudad(request.getCiudad());
        }
        if (request.getTipoContrato() != null) {
            contrato.setTipoContrato(TipoContrato.valueOf(request.getTipoContrato()));
        }
        if (request.getPrecioAutorizadoVenta() != null) {
            contrato.setPrecioAutorizadoVenta(request.getPrecioAutorizadoVenta());
        }
        if (request.getPrecioMinimoAutorizado() != null) {
            contrato.setPrecioMinimoAutorizado(request.getPrecioMinimoAutorizado());
        }
        if (request.getPorcentajeComision() != null) {
            contrato.setPorcentajeComision(request.getPorcentajeComision());
        }
        if (request.getValorFijoComision() != null) {
            contrato.setValorFijoComision(request.getValorFijoComision());
        }
        if (request.getSobreprecioComision() != null) {
            contrato.setSobreprecioComision(request.getSobreprecioComision());
        }
        if (request.getFormaPagoComision() != null) {
            contrato.setFormaPagoComision(FormaPagoComision.valueOf(request.getFormaPagoComision()));
        }
        if (request.getFechaVigenciaHasta() != null) {
            contrato.setFechaVigenciaHasta(request.getFechaVigenciaHasta());
        }
        if (request.getOpcionFirma() != null) {
            contrato.setOpcionFirma(OpcionFirma.valueOf(request.getOpcionFirma()));
        }
        if (request.getTextoContrato() != null) {
            contrato.setTextoContrato(request.getTextoContrato());
            contrato.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                    "Contrato de corretaje " + contrato.getNumeroContrato(),
                    contrato.getTextoContrato(), contrato.getPdfUrl()));
        }

        ContratoCorretaje actualizado = contratoCorretajeRepository.save(contrato);

        registrarHistorial(actualizado, "Datos del contrato actualizados");

        return mapearAResponse(actualizado);
    }

    public ContratoCorretajeResponse cambiarEstado(UUID id, CambiarEstadoContratoCorretajeRequest request) {
        ContratoCorretaje contrato = obtenerContrato(id);

        EstadoDocumento nuevoEstado = EstadoDocumento.valueOf(request.getNuevoEstado());

        if (ESTADOS_QUE_EXIGEN_CAMPOS_COMPLETOS.contains(nuevoEstado)) {
            validarCamposObligatorios(contrato);
        }

        contrato.setEstado(nuevoEstado);

        ContratoCorretaje actualizado = contratoCorretajeRepository.save(contrato);

        registrarHistorial(actualizado, "Estado cambiado a " + nuevoEstado.name());

        return mapearAResponse(actualizado);
    }

    public ResumenContratosCorretajeResponse resumenPorEstado() {
        List<ContratoCorretaje> todos = contratoCorretajeRepository.findAll();
        LocalDate limiteProximoAVencer = LocalDate.now().plusDays(30);

        long vigentes = todos.stream().filter(c -> c.getEstado() == EstadoDocumento.VIGENTE).count();
        long proximosAVencer = todos.stream()
                .filter(c -> c.getEstado() == EstadoDocumento.VIGENTE)
                .filter(c -> c.getFechaVigenciaHasta() != null && !c.getFechaVigenciaHasta().isAfter(limiteProximoAVencer))
                .count();
        long renovados = todos.stream().filter(c -> c.getEstado() == EstadoDocumento.RENOVADO).count();
        long finalizados = todos.stream()
                .filter(c -> c.getEstado() == EstadoDocumento.VENCIDO || c.getEstado() == EstadoDocumento.CANCELADO)
                .count();

        return ResumenContratosCorretajeResponse.builder()
                .total(todos.size())
                .vigentes(vigentes)
                .proximosAVencer(proximosAVencer)
                .renovados(renovados)
                .finalizados(finalizados)
                .build();
    }

    private void validarCamposObligatorios(ContratoCorretaje contrato) {
        List<String> faltantes = new java.util.ArrayList<>();

        if (contrato.getTipoContrato() == null) faltantes.add("tipoContrato");
        if (contrato.getPrecioAutorizadoVenta() == null) faltantes.add("precioAutorizadoVenta");
        if (contrato.getFormaPagoComision() == null) faltantes.add("formaPagoComision");
        if (contrato.getOpcionFirma() == null) faltantes.add("opcionFirma");
        if (contrato.getFechaVigenciaHasta() == null) faltantes.add("fechaVigenciaHasta");
        if (contrato.getCiudad() == null || contrato.getCiudad().isBlank()) faltantes.add("ciudad");

        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Faltan campos obligatorios para avanzar el estado del contrato: " + String.join(", ", faltantes));
        }
    }

    private ContratoCorretaje obtenerContrato(UUID id) {
        return contratoCorretajeRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Contrato de corretaje no encontrado con id: " + id));
    }

    private void registrarHistorial(ContratoCorretaje contrato, String evento) {
        HistorialContratoCorretaje historial = HistorialContratoCorretaje.builder()
                .contratoCorretaje(contrato)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialContratoCorretajeRepository.save(historial);
    }

    private ContratoCorretajeResponse mapearAResponse(ContratoCorretaje contrato) {
        return ContratoCorretajeResponse.builder()
                .id(contrato.getId())
                .numeroContrato(contrato.getNumeroContrato())
                .inmuebleId(contrato.getInmueble().getId())
                .inmuebleCodigo(contrato.getInmueble().getCodigo())
                .propietarioId(contrato.getPropietario().getId())
                .propietarioNombre(contrato.getPropietario().getNombre())
                .fechaElaboracion(contrato.getFechaElaboracion())
                .ciudad(contrato.getCiudad())
                .tipoContrato(contrato.getTipoContrato() != null ? contrato.getTipoContrato().name() : null)
                .precioAutorizadoVenta(contrato.getPrecioAutorizadoVenta())
                .precioMinimoAutorizado(contrato.getPrecioMinimoAutorizado())
                .porcentajeComision(contrato.getPorcentajeComision())
                .valorFijoComision(contrato.getValorFijoComision())
                .sobreprecioComision(contrato.getSobreprecioComision())
                .formaPagoComision(contrato.getFormaPagoComision() != null ? contrato.getFormaPagoComision().name() : null)
                .fechaVigenciaHasta(contrato.getFechaVigenciaHasta())
                .opcionFirma(contrato.getOpcionFirma() != null ? contrato.getOpcionFirma().name() : null)
                .estado(contrato.getEstado().name())
                .textoContrato(contrato.getTextoContrato())
                .fechaGeneracionTexto(contrato.getFechaGeneracionTexto())
                .pdfUrl(contrato.getPdfUrl())
                .createdAt(contrato.getCreatedAt())
                .build();
    }
}
