package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.ActualizarPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.CambiarEstadoPromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PromesaCompraventaRequest;
import com.felipeparodi.crmbackend.documentacion.dto.PromesaCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.dto.ResumenPromesasCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.EstadoDocumento;
import com.felipeparodi.crmbackend.documentacion.entity.OpcionFirma;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.documentacion.util.GeneradorNumeroPromesaCompraventa;
import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.RolParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.EnumSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class PromesaCompraventaService {

    private static final Set<EstadoDocumento> ESTADOS_QUE_EXIGEN_CAMPOS_COMPLETOS = EnumSet.of(
            EstadoDocumento.EN_REVISION,
            EstadoDocumento.ENVIADO,
            EstadoDocumento.PENDIENTE_FIRMA,
            EstadoDocumento.FIRMADO,
            EstadoDocumento.VIGENTE);

    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final NegociacionRepository negociacionRepository;
    private final ParteNegociacionRepository parteNegociacionRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final GeneradorNumeroPromesaCompraventa generadorNumeroPromesaCompraventa;
    private final PdfDocumentoService pdfDocumentoService;

    private static final String SUBCARPETA_PDF = "promesas-compraventa-pdf";

    public PromesaCompraventaService(PromesaCompraventaRepository promesaCompraventaRepository,
                                      NegociacionRepository negociacionRepository,
                                      ParteNegociacionRepository parteNegociacionRepository,
                                      HistorialNegociacionRepository historialNegociacionRepository,
                                      GeneradorNumeroPromesaCompraventa generadorNumeroPromesaCompraventa,
                                      PdfDocumentoService pdfDocumentoService) {
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.negociacionRepository = negociacionRepository;
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.generadorNumeroPromesaCompraventa = generadorNumeroPromesaCompraventa;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public PromesaCompraventaResponse crear(PromesaCompraventaRequest request) {
        Negociacion negociacion = negociacionRepository.findById(request.getNegociacionId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Negociacion no encontrada con id: " + request.getNegociacionId()));

        if (promesaCompraventaRepository.findByNegociacionId(negociacion.getId()).isPresent()) {
            throw new IllegalArgumentException("Ya existe una promesa de compraventa para esta negociacion");
        }

        PromesaCompraventa promesa = PromesaCompraventa.builder()
                .numeroContrato(generadorNumeroPromesaCompraventa.generar())
                .negociacion(negociacion)
                .fechaElaboracion(request.getFechaElaboracion())
                .ciudad(request.getCiudad())
                .opcionFirma(request.getOpcionFirma() != null ? OpcionFirma.valueOf(request.getOpcionFirma()) : null)
                .build();

        PromesaCompraventa guardada = promesaCompraventaRepository.save(promesa);

        registrarHistorialNegocio(negociacion, "Promesa de compraventa creada: " + guardada.getNumeroContrato());

        return mapearAResponse(guardada);
    }

    public List<PromesaCompraventaResponse> listarTodos() {
        return promesaCompraventaRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public PromesaCompraventaResponse buscarPorId(UUID id) {
        return mapearAResponse(obtenerPromesa(id));
    }

    public PromesaCompraventaResponse actualizar(UUID id, ActualizarPromesaCompraventaRequest request) {
        PromesaCompraventa promesa = obtenerPromesa(id);

        if (request.getCiudad() != null) {
            promesa.setCiudad(request.getCiudad());
        }
        if (request.getOpcionFirma() != null) {
            promesa.setOpcionFirma(OpcionFirma.valueOf(request.getOpcionFirma()));
        }
        if (request.getTextoPromesa() != null) {
            promesa.setTextoPromesa(request.getTextoPromesa());
            promesa.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                    "Promesa de compraventa " + promesa.getNumeroContrato(),
                    promesa.getTextoPromesa(), promesa.getPdfUrl()));
        }

        PromesaCompraventa actualizada = promesaCompraventaRepository.save(promesa);

        registrarHistorialNegocio(actualizada.getNegociacion(),
                "Datos de la promesa de compraventa " + actualizada.getNumeroContrato() + " actualizados");

        return mapearAResponse(actualizada);
    }

    public PromesaCompraventaResponse cambiarEstado(UUID id, CambiarEstadoPromesaCompraventaRequest request) {
        PromesaCompraventa promesa = obtenerPromesa(id);

        EstadoDocumento nuevoEstado = EstadoDocumento.valueOf(request.getNuevoEstado());

        if (ESTADOS_QUE_EXIGEN_CAMPOS_COMPLETOS.contains(nuevoEstado)) {
            validarCamposObligatorios(promesa);
        }

        promesa.setEstado(nuevoEstado);

        PromesaCompraventa actualizada = promesaCompraventaRepository.save(promesa);

        registrarHistorialNegocio(actualizada.getNegociacion(),
                "Estado de la promesa " + actualizada.getNumeroContrato() + " cambiado a " + nuevoEstado.name());

        return mapearAResponse(actualizada);
    }

    public ResumenPromesasCompraventaResponse resumenPorEstado() {
        List<PromesaCompraventa> todas = promesaCompraventaRepository.findAll();

        long vigentes = todas.stream().filter(p -> p.getEstado() == EstadoDocumento.VIGENTE).count();
        long renovados = todas.stream().filter(p -> p.getEstado() == EstadoDocumento.RENOVADO).count();
        long finalizados = todas.stream()
                .filter(p -> p.getEstado() == EstadoDocumento.VENCIDO || p.getEstado() == EstadoDocumento.CANCELADO)
                .count();

        return ResumenPromesasCompraventaResponse.builder()
                .total(todas.size())
                .vigentes(vigentes)
                .renovados(renovados)
                .finalizados(finalizados)
                .build();
    }

    private void validarCamposObligatorios(PromesaCompraventa promesa) {
        List<String> faltantes = new java.util.ArrayList<>();

        if (promesa.getCiudad() == null || promesa.getCiudad().isBlank()) faltantes.add("ciudad");
        if (promesa.getOpcionFirma() == null) faltantes.add("opcionFirma");
        if (promesa.getFechaElaboracion() == null) faltantes.add("fechaElaboracion");

        if (!faltantes.isEmpty()) {
            throw new IllegalArgumentException(
                    "Faltan campos obligatorios para avanzar el estado de la promesa: " + String.join(", ", faltantes));
        }
    }

    private PromesaCompraventa obtenerPromesa(UUID id) {
        return promesaCompraventaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Promesa de compraventa no encontrada con id: " + id));
    }

    private void registrarHistorialNegocio(Negociacion negociacion, String evento) {
        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private PromesaCompraventaResponse mapearAResponse(PromesaCompraventa promesa) {
        Negociacion negociacion = promesa.getNegociacion();

        List<ParteNegociacion> partes = parteNegociacionRepository.findByNegociacionId(negociacion.getId());

        List<ParteNegociacionResponse> compradores = partes.stream()
                .filter(p -> p.getRol() == RolParteNegociacion.COMPRADOR)
                .map(this::mapearParte)
                .toList();

        List<ParteNegociacionResponse> vendedores = partes.stream()
                .filter(p -> p.getRol() == RolParteNegociacion.VENDEDOR)
                .map(this::mapearParte)
                .toList();

        return PromesaCompraventaResponse.builder()
                .id(promesa.getId())
                .numeroContrato(promesa.getNumeroContrato())
                .negociacionId(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .inmuebleId(negociacion.getInmueble().getId())
                .inmuebleCodigo(negociacion.getInmueble().getCodigo())
                .compradores(compradores)
                .vendedores(vendedores)
                .fechaElaboracion(promesa.getFechaElaboracion())
                .ciudad(promesa.getCiudad())
                .opcionFirma(promesa.getOpcionFirma() != null ? promesa.getOpcionFirma().name() : null)
                .estado(promesa.getEstado().name())
                .textoPromesa(promesa.getTextoPromesa())
                .fechaGeneracionTexto(promesa.getFechaGeneracionTexto())
                .pdfUrl(promesa.getPdfUrl())
                .createdAt(promesa.getCreatedAt())
                .build();
    }

    private ParteNegociacionResponse mapearParte(ParteNegociacion parte) {
        return ParteNegociacionResponse.builder()
                .id(parte.getId())
                .negociacionId(parte.getNegociacion().getId())
                .rol(parte.getRol().name())
                .orden(parte.getOrden())
                .tipoDocumento(parte.getTipoDocumento())
                .numeroDocumento(parte.getNumeroDocumento())
                .estadoCivil(parte.getEstadoCivil())
                .direccion(parte.getDireccion())
                .correoElectronico(parte.getCorreoElectronico())
                .build();
    }
}
