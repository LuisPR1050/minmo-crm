package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.documentacion.dto.PromesaCompraventaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventaClausula;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionResponse;
import com.felipeparodi.crmbackend.negociacion.entity.BienAsociado;
import com.felipeparodi.crmbackend.negociacion.entity.DistribucionGastos;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.RolParteNegociacion;
import com.felipeparodi.crmbackend.negociacion.repository.BienAsociadoRepository;
import com.felipeparodi.crmbackend.negociacion.repository.DistribucionGastosRepository;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.documentacion.util.LimpiadorMarkdownIA;
import com.felipeparodi.crmbackend.negociacion.repository.ParteNegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RedaccionPromesaCompraventaIAService {

    private static final String MODELO = "claude-opus-5";

    private static final String DISCLAIMER = "\n\n---\nBORRADOR GENERADO AUTOMATICAMENTE POR IA. "
            + "Requiere revision de un abogado antes de ser firmado. Los campos marcados como "
            + "[DATO FALTANTE] deben completarse manualmente.";

    private static final String SUBCARPETA_PDF = "promesas-compraventa-pdf";

    private final AnthropicClient anthropicClient;
    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final PromesaCompraventaClausulaRepository promesaCompraventaClausulaRepository;
    private final ParteNegociacionRepository parteNegociacionRepository;
    private final BienAsociadoRepository bienAsociadoRepository;
    private final DistribucionGastosRepository distribucionGastosRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final PdfDocumentoService pdfDocumentoService;

    public RedaccionPromesaCompraventaIAService(AnthropicClient anthropicClient,
                                                 PromesaCompraventaRepository promesaCompraventaRepository,
                                                 PromesaCompraventaClausulaRepository promesaCompraventaClausulaRepository,
                                                 ParteNegociacionRepository parteNegociacionRepository,
                                                 BienAsociadoRepository bienAsociadoRepository,
                                                 DistribucionGastosRepository distribucionGastosRepository,
                                                 HistorialNegociacionRepository historialNegociacionRepository,
                                                 PdfDocumentoService pdfDocumentoService) {
        this.anthropicClient = anthropicClient;
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.promesaCompraventaClausulaRepository = promesaCompraventaClausulaRepository;
        this.parteNegociacionRepository = parteNegociacionRepository;
        this.bienAsociadoRepository = bienAsociadoRepository;
        this.distribucionGastosRepository = distribucionGastosRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public PromesaCompraventaResponse generarTexto(UUID promesaCompraventaId, String instruccionesAdicionales) {
        PromesaCompraventa promesa = promesaCompraventaRepository.findById(promesaCompraventaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Promesa de compraventa no encontrada con id: " + promesaCompraventaId));

        String prompt = construirPrompt(promesa, instruccionesAdicionales);

        MessageCreateParams params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(8000L)
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.HIGH).build())
                .addUserMessage(prompt)
                .build();

        Message response;
        try {
            response = anthropicClient.messages().create(params);
        } catch (com.anthropic.errors.AnthropicException ex) {
            throw new ServicioIANoDisponibleException(
                    "No se pudo redactar la promesa de compraventa: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        String textoGenerado = response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(block -> block.text())
                .collect(Collectors.joining("\n"));

        if (textoGenerado.isBlank()) {
            throw new IllegalStateException(
                    "La IA no genero texto para la promesa " + promesaCompraventaId);
        }

        textoGenerado = LimpiadorMarkdownIA.limpiar(textoGenerado);

        promesa.setTextoPromesa(textoGenerado + DISCLAIMER);
        promesa.setFechaGeneracionTexto(LocalDateTime.now());
        promesa.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                "Promesa de compraventa " + promesa.getNumeroContrato(),
                promesa.getTextoPromesa(), promesa.getPdfUrl()));

        PromesaCompraventa guardada = promesaCompraventaRepository.save(promesa);

        registrarHistorialNegocio(guardada.getNegociacion(),
                "Texto de la promesa " + guardada.getNumeroContrato() + " generado por IA");

        return mapearAResponse(guardada);
    }

    private String construirPrompt(PromesaCompraventa promesa, String instruccionesAdicionales) {
        Negociacion negociacion = promesa.getNegociacion();
        Inmueble inmueble = negociacion.getInmueble();

        List<ParteNegociacion> partes = parteNegociacionRepository.findByNegociacionId(negociacion.getId());
        List<ParteNegociacion> compradores = partes.stream()
                .filter(pn -> pn.getRol() == RolParteNegociacion.COMPRADOR)
                .collect(Collectors.toList());
        List<ParteNegociacion> vendedores = partes.stream()
                .filter(pn -> pn.getRol() == RolParteNegociacion.VENDEDOR)
                .collect(Collectors.toList());

        List<BienAsociado> bienes = bienAsociadoRepository.findByNegociacionId(negociacion.getId());
        DistribucionGastos gastos = distribucionGastosRepository.findByNegociacionId(negociacion.getId())
                .orElse(null);

        List<String> clausulas = promesaCompraventaClausulaRepository
                .findByPromesaCompraventaId(promesa.getId())
                .stream()
                .filter(PromesaCompraventaClausula::isIncluida)
                .map(c -> c.getClausulaPromesa().getNombre())
                .collect(Collectors.toList());

        StringBuilder p = new StringBuilder();
        p.append("Eres un asistente legal que redacta promesas de compraventa de inmuebles en Colombia. ");
        p.append("Redacta el texto completo de la siguiente promesa de compraventa en espanol, con lenguaje ");
        p.append("juridico formal, numerando las clausulas. Incluye UNICAMENTE las clausulas listadas abajo, ");
        p.append("desarrollando cada una con el contenido juridico habitual para ese tipo de clausula en una ");
        p.append("promesa de compraventa colombiana. No inventes datos que no esten en esta lista: donde falte ");
        p.append("un dato obligatorio (por ejemplo, numero de cedula de una de las partes), usa el marcador ");
        p.append("exacto [DATO FALTANTE] en su lugar. Escribe en prosa clara, profesional y bien organizada. ");
        p.append("No uses formato Markdown: nada de asteriscos (*), dobles asteriscos (**) ni numerales (#); ");
        p.append("el resultado debe ser texto plano listo para un documento legal, sin simbolos de marcado.\n\n");

        p.append("DATOS DEL CONTRATO\n");
        p.append("Numero de contrato: ").append(promesa.getNumeroContrato()).append("\n");
        p.append("Numero de negocio: ").append(negociacion.getNumeroNegocio()).append("\n");
        p.append("Fecha de elaboracion: ").append(promesa.getFechaElaboracion()).append("\n");
        p.append("Ciudad: ").append(promesa.getCiudad()).append("\n");
        if (promesa.getOpcionFirma() != null) {
            p.append("Opcion de firma: ").append(promesa.getOpcionFirma()).append("\n");
        }

        p.append("\nPROMITENTE(S) VENDEDOR(ES)\n");
        if (vendedores.isEmpty()) {
            p.append("[DATO FALTANTE]\n");
        } else {
            vendedores.forEach(v -> agregarParte(p, v));
        }

        p.append("\nPROMITENTE(S) COMPRADOR(ES)\n");
        if (compradores.isEmpty()) {
            p.append("[DATO FALTANTE]\n");
        } else {
            compradores.forEach(c -> agregarParte(p, c));
        }

        p.append("\nDATOS DEL INMUEBLE\n");
        p.append("Codigo interno: ").append(inmueble.getCodigo()).append("\n");
        p.append("Tipo de propiedad: ").append(inmueble.getTipoPropiedad()).append("\n");
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        p.append("Direccion: ").append(inmueble.getDireccionExacta() != null ? inmueble.getDireccionExacta() : "[DATO FALTANTE]").append("\n");
        p.append("Matricula inmobiliaria: ").append(inmueble.getMatriculaInmobiliaria() != null ? inmueble.getMatriculaInmobiliaria() : "[DATO FALTANTE]").append("\n");

        p.append("\nPRECIO Y CONDICIONES\n");
        if (negociacion.getPrecioFinalAcordado() != null) {
            p.append("Precio final acordado: ").append(negociacion.getPrecioFinalAcordado()).append(" COP\n");
        }
        if (negociacion.getFechaFirmaPcv() != null) {
            p.append("Fecha programada de firma de escritura: ").append(negociacion.getFechaFirmaPcv()).append("\n");
        }
        if (negociacion.getFechaEntregaProgramada() != null) {
            p.append("Fecha programada de entrega: ").append(negociacion.getFechaEntregaProgramada()).append("\n");
        }
        if (negociacion.getEntidadFinanciera() != null) {
            p.append("Entidad financiera: ").append(negociacion.getEntidadFinanciera()).append("\n");
        }
        if (negociacion.getFinanciacionDirectaCondiciones() != null) {
            p.append("Financiacion directa: ").append(negociacion.getFinanciacionDirectaCondiciones()).append("\n");
        }
        if (negociacion.getPermutaCondiciones() != null) {
            p.append("Permuta: ").append(negociacion.getPermutaCondiciones()).append("\n");
        }

        if (gastos != null) {
            p.append("\nDISTRIBUCION DE GASTOS\n");
            if (gastos.getGastosEscrituracion() != null) {
                p.append("Gastos de escrituracion: ").append(gastos.getGastosEscrituracion()).append("\n");
            }
            if (gastos.getRetencionFuente() != null) {
                p.append("Retencion en la fuente: ").append(gastos.getRetencionFuente()).append("\n");
            }
            if (gastos.getGastosRegistro() != null) {
                p.append("Gastos de registro: ").append(gastos.getGastosRegistro()).append("\n");
            }
            if (gastos.getGastosCancelacionGravamen() != null) {
                p.append("Gastos de cancelacion de gravamen: ").append(gastos.getGastosCancelacionGravamen()).append("\n");
            }
            if (gastos.getGastosConstitucionGravamen() != null) {
                p.append("Gastos de constitucion de gravamen: ").append(gastos.getGastosConstitucionGravamen()).append("\n");
            }
        }

        if (!bienes.isEmpty()) {
            p.append("\nBIENES ASOCIADOS INCLUIDOS EN LA VENTA\n");
            bienes.forEach(b -> p.append("- ").append(b.getTipo())
                    .append(b.getMatriculaInmobiliaria() != null ? " (matricula " + b.getMatriculaInmobiliaria() + ")" : "")
                    .append("\n"));
        }

        p.append("\nCLAUSULAS A INCLUIR (en este orden)\n");
        if (clausulas.isEmpty()) {
            p.append("(No se han seleccionado clausulas todavia; redacta solo el encabezado y los datos de las partes)\n");
        } else {
            clausulas.forEach(c -> p.append("- ").append(c).append("\n"));
        }

        if (instruccionesAdicionales != null && !instruccionesAdicionales.isBlank()) {
            p.append("\nINSTRUCCIONES ADICIONALES DEL USUARIO (ten en cuenta esto al redactar)\n");
            p.append(instruccionesAdicionales.trim()).append("\n");
        }

        return p.toString();
    }

    private void agregarParte(StringBuilder p, ParteNegociacion parte) {
        p.append("- Nombre: [DATO FALTANTE] | ");
        p.append("Documento: ").append(parte.getTipoDocumento() != null ? parte.getTipoDocumento() : "[DATO FALTANTE]")
                .append(" ").append(parte.getNumeroDocumento() != null ? parte.getNumeroDocumento() : "[DATO FALTANTE]").append(" | ");
        p.append("Estado civil: ").append(parte.getEstadoCivil() != null ? parte.getEstadoCivil() : "[DATO FALTANTE]").append(" | ");
        p.append("Direccion: ").append(parte.getDireccion() != null ? parte.getDireccion() : "[DATO FALTANTE]").append("\n");
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
                .collect(Collectors.toList());

        List<ParteNegociacionResponse> vendedores = partes.stream()
                .filter(p -> p.getRol() == RolParteNegociacion.VENDEDOR)
                .map(this::mapearParte)
                .collect(Collectors.toList());

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
