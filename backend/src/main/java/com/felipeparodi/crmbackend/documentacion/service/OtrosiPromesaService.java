package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ActualizarOtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.OtrosiPromesaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.OtrosiPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.JustificacionOtrosi;
import com.felipeparodi.crmbackend.documentacion.entity.MotivoOtrosi;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesa;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroOtrosiPromesa;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class OtrosiPromesaService {

    private final OtrosiPromesaRepository otrosiPromesaRepository;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final NegociacionRepository negociacionRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final GeneradorNumeroOtrosiPromesa generadorNumeroOtrosiPromesa;
    private final PdfDocumentoService pdfDocumentoService;

    private static final String SUBCARPETA_PDF = "otrosis-promesa-pdf";

    public OtrosiPromesaService(OtrosiPromesaRepository otrosiPromesaRepository,
                                 PromesaCompraventaRepository promesaCompraventaRepository,
                                 NegociacionRepository negociacionRepository,
                                 HistorialNegociacionRepository historialNegociacionRepository,
                                 GeneradorNumeroOtrosiPromesa generadorNumeroOtrosiPromesa,
                                 PdfDocumentoService pdfDocumentoService) {
        this.otrosiPromesaRepository = otrosiPromesaRepository;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.negociacionRepository = negociacionRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.generadorNumeroOtrosiPromesa = generadorNumeroOtrosiPromesa;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public OtrosiPromesaResponse crear(OtrosiPromesaRequest request) {
        PromesaCompraventa promesa = promesaCompraventaRepository.findById(request.getPromesaCompraventaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Promesa de compraventa no encontrada con id: " + request.getPromesaCompraventaId()));

        if (request.getMotivo() == null) {
            throw new IllegalArgumentException("El motivo del otrosi es obligatorio");
        }

        OtrosiPromesa otrosi = OtrosiPromesa.builder()
                .numeroOtrosi(generadorNumeroOtrosiPromesa.generar())
                .promesaCompraventa(promesa)
                .fechaElaboracion(request.getFechaElaboracion())
                .motivo(MotivoOtrosi.valueOf(request.getMotivo()))
                .otroMotivoTexto(request.getOtroMotivoTexto())
                .justificacion(request.getJustificacion() != null
                        ? JustificacionOtrosi.valueOf(request.getJustificacion()) : null)
                .nuevoPrecio(request.getNuevoPrecio())
                .nuevaFechaEscritura(request.getNuevaFechaEscritura())
                .nuevaFechaEntrega(request.getNuevaFechaEntrega())
                .nuevaDistribucionGastos(request.getNuevaDistribucionGastos())
                .nuevosBienesIncluidos(request.getNuevosBienesIncluidos())
                .nuevasCondicionesEspeciales(request.getNuevasCondicionesEspeciales())
                .observaciones(request.getObservaciones())
                .build();

        OtrosiPromesa guardado = otrosiPromesaRepository.save(otrosi);

        registrarHistorialNegocio(promesa.getNegociacion(),
                "Otrosi creado: " + guardado.getNumeroOtrosi() + " (motivo: " + guardado.getMotivo().name() + ")");

        return mapearAResponse(guardado);
    }

    public List<OtrosiPromesaResponse> listarTodos() {
        return otrosiPromesaRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<OtrosiPromesaResponse> listarPorPromesa(UUID promesaCompraventaId) {
        return otrosiPromesaRepository.findByPromesaCompraventaId(promesaCompraventaId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public OtrosiPromesaResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerOtrosi(id));
    }

    public OtrosiPromesaResponse actualizar(UUID id, ActualizarOtrosiPromesaRequest request) {
        OtrosiPromesa otrosi = obtenerOtrosi(id);

        if (otrosi.isAplicado()) {
            throw new IllegalArgumentException("No se puede editar un otrosi que ya fue aplicado");
        }

        if (request.getMotivo() != null) {
            otrosi.setMotivo(MotivoOtrosi.valueOf(request.getMotivo()));
        }
        if (request.getOtroMotivoTexto() != null) {
            otrosi.setOtroMotivoTexto(request.getOtroMotivoTexto());
        }
        if (request.getJustificacion() != null) {
            otrosi.setJustificacion(JustificacionOtrosi.valueOf(request.getJustificacion()));
        }
        if (request.getNuevoPrecio() != null) {
            otrosi.setNuevoPrecio(request.getNuevoPrecio());
        }
        if (request.getNuevaFechaEscritura() != null) {
            otrosi.setNuevaFechaEscritura(request.getNuevaFechaEscritura());
        }
        if (request.getNuevaFechaEntrega() != null) {
            otrosi.setNuevaFechaEntrega(request.getNuevaFechaEntrega());
        }
        if (request.getNuevaDistribucionGastos() != null) {
            otrosi.setNuevaDistribucionGastos(request.getNuevaDistribucionGastos());
        }
        if (request.getNuevosBienesIncluidos() != null) {
            otrosi.setNuevosBienesIncluidos(request.getNuevosBienesIncluidos());
        }
        if (request.getNuevasCondicionesEspeciales() != null) {
            otrosi.setNuevasCondicionesEspeciales(request.getNuevasCondicionesEspeciales());
        }
        if (request.getObservaciones() != null) {
            otrosi.setObservaciones(request.getObservaciones());
        }
        if (request.getTextoOtrosi() != null) {
            otrosi.setTextoOtrosi(request.getTextoOtrosi());
            otrosi.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                    "Otrosi " + otrosi.getNumeroOtrosi(),
                    otrosi.getTextoOtrosi(), otrosi.getPdfUrl()));
        }

        OtrosiPromesa actualizado = otrosiPromesaRepository.save(otrosi);

        return mapearAResponse(actualizado);
    }

    public OtrosiPromesaResponse aplicar(UUID id) {
        OtrosiPromesa otrosi = obtenerOtrosi(id);

        if (otrosi.isAplicado()) {
            throw new IllegalArgumentException("El otrosi " + otrosi.getNumeroOtrosi() + " ya fue aplicado");
        }

        PromesaCompraventa promesa = otrosi.getPromesaCompraventa();
        Negociacion negociacion = promesa.getNegociacion();

        List<String> cambios = new ArrayList<>();
        boolean negociacionModificada = false;

        if (otrosi.getNuevoPrecio() != null) {
            negociacion.setPrecioFinalAcordado(otrosi.getNuevoPrecio());
            negociacionModificada = true;
            cambios.add("precio final acordado actualizado a " + otrosi.getNuevoPrecio());
        }

        if (otrosi.getNuevaFechaEscritura() != null) {
            negociacion.setFechaFirmaPcv(otrosi.getNuevaFechaEscritura());
            negociacionModificada = true;
            promesa.setAlertaFirmaEnviada(false);
            promesaCompraventaRepository.save(promesa);
            cambios.add("fecha de firma de escritura reprogramada a " + otrosi.getNuevaFechaEscritura());
        }

        if (otrosi.getNuevaFechaEntrega() != null) {
            negociacion.setFechaEntregaProgramada(otrosi.getNuevaFechaEntrega());
            negociacionModificada = true;
            cambios.add("fecha de entrega reprogramada a " + otrosi.getNuevaFechaEntrega());
        }

        if (negociacionModificada) {
            negociacionRepository.save(negociacion);
        }

        otrosi.setAplicado(true);
        otrosi.setFechaAplicacion(LocalDateTime.now());

        OtrosiPromesa aplicado = otrosiPromesaRepository.save(otrosi);

        String evento = "Otrosi " + aplicado.getNumeroOtrosi() + " aplicado"
                + (cambios.isEmpty() ? "" : ": " + String.join("; ", cambios));

        registrarHistorialNegocio(negociacion, evento);

        return mapearAResponse(aplicado);
    }

    private OtrosiPromesa obtenerOtrosi(UUID id) {
        return otrosiPromesaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Otrosi no encontrado con id: " + id));
    }

    private void registrarHistorialNegocio(Negociacion negociacion, String evento) {
        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private OtrosiPromesaResponse mapearAResponse(OtrosiPromesa otrosi) {
        PromesaCompraventa promesa = otrosi.getPromesaCompraventa();
        Negociacion negociacion = promesa.getNegociacion();

        return OtrosiPromesaResponse.builder()
                .id(otrosi.getId())
                .numeroOtrosi(otrosi.getNumeroOtrosi())
                .promesaCompraventaId(promesa.getId())
                .numeroContratoPromesa(promesa.getNumeroContrato())
                .negociacionId(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .fechaElaboracion(otrosi.getFechaElaboracion())
                .motivo(otrosi.getMotivo() != null ? otrosi.getMotivo().name() : null)
                .otroMotivoTexto(otrosi.getOtroMotivoTexto())
                .justificacion(otrosi.getJustificacion() != null ? otrosi.getJustificacion().name() : null)
                .nuevoPrecio(otrosi.getNuevoPrecio())
                .nuevaFechaEscritura(otrosi.getNuevaFechaEscritura())
                .nuevaFechaEntrega(otrosi.getNuevaFechaEntrega())
                .nuevaDistribucionGastos(otrosi.getNuevaDistribucionGastos())
                .nuevosBienesIncluidos(otrosi.getNuevosBienesIncluidos())
                .nuevasCondicionesEspeciales(otrosi.getNuevasCondicionesEspeciales())
                .observaciones(otrosi.getObservaciones())
                .aplicado(otrosi.isAplicado())
                .fechaAplicacion(otrosi.getFechaAplicacion())
                .textoOtrosi(otrosi.getTextoOtrosi())
                .fechaGeneracionTexto(otrosi.getFechaGeneracionTexto())
                .pdfUrl(otrosi.getPdfUrl())
                .createdAt(otrosi.getCreatedAt())
                .build();
    }
}
