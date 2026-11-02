package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.felipeparodi.crmbackend.documentacion.dto.OtrosiPromesaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesa;
import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesaClausula;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.OtrosiPromesaRepository;
import com.felipeparodi.crmbackend.documentacion.util.LimpiadorMarkdownIA;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RedaccionOtrosiPromesaIAService {

    private static final String MODELO = "claude-opus-5";

    private static final String DISCLAIMER = "\n\n---\nBORRADOR GENERADO AUTOMATICAMENTE POR IA. "
            + "Requiere revision de un abogado antes de ser firmado. Los campos marcados como "
            + "[DATO FALTANTE] deben completarse manualmente.";

    private static final String SUBCARPETA_PDF = "otrosis-promesa-pdf";

    private final AnthropicClient anthropicClient;
    private final OtrosiPromesaRepository otrosiPromesaRepository;
    private final OtrosiPromesaClausulaRepository otrosiPromesaClausulaRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;
    private final PdfDocumentoService pdfDocumentoService;

    public RedaccionOtrosiPromesaIAService(AnthropicClient anthropicClient,
                                            OtrosiPromesaRepository otrosiPromesaRepository,
                                            OtrosiPromesaClausulaRepository otrosiPromesaClausulaRepository,
                                            HistorialNegociacionRepository historialNegociacionRepository,
                                            PdfDocumentoService pdfDocumentoService) {
        this.anthropicClient = anthropicClient;
        this.otrosiPromesaRepository = otrosiPromesaRepository;
        this.otrosiPromesaClausulaRepository = otrosiPromesaClausulaRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public OtrosiPromesaResponse generarTexto(UUID otrosiPromesaId, String instruccionesAdicionales) {
        OtrosiPromesa otrosi = otrosiPromesaRepository.findById(otrosiPromesaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Otrosi no encontrado con id: " + otrosiPromesaId));

        String prompt = construirPrompt(otrosi, instruccionesAdicionales);

        MessageCreateParams params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(6000L)
                .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.HIGH).build())
                .addUserMessage(prompt)
                .build();

        Message response;
        try {
            response = anthropicClient.messages().create(params);
        } catch (com.anthropic.errors.AnthropicException ex) {
            throw new ServicioIANoDisponibleException(
                    "No se pudo redactar el otrosi: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        String textoGenerado = response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(block -> block.text())
                .collect(Collectors.joining("\n"));

        if (textoGenerado.isBlank()) {
            throw new IllegalStateException("La IA no genero texto para el otrosi " + otrosiPromesaId);
        }

        textoGenerado = LimpiadorMarkdownIA.limpiar(textoGenerado);

        otrosi.setTextoOtrosi(textoGenerado + DISCLAIMER);
        otrosi.setFechaGeneracionTexto(LocalDateTime.now());
        otrosi.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                "Otrosi " + otrosi.getNumeroOtrosi(),
                otrosi.getTextoOtrosi(), otrosi.getPdfUrl()));

        OtrosiPromesa guardado = otrosiPromesaRepository.save(otrosi);

        registrarHistorialNegocio(guardado.getPromesaCompraventa().getNegociacion(),
                "Texto del otrosi " + guardado.getNumeroOtrosi() + " generado por IA");

        return mapearAResponse(guardado);
    }

    private String construirPrompt(OtrosiPromesa otrosi, String instruccionesAdicionales) {
        PromesaCompraventa promesa = otrosi.getPromesaCompraventa();
        Negociacion negociacion = promesa.getNegociacion();

        List<String> clausulas = otrosiPromesaClausulaRepository.findByOtrosiPromesaId(otrosi.getId())
                .stream()
                .filter(OtrosiPromesaClausula::isIncluida)
                .map(c -> c.getClausulaCorretaje().getNombre())
                .collect(Collectors.toList());

        StringBuilder p = new StringBuilder();
        p.append("Eres un asistente legal que redacta otrosies (modificaciones) a promesas de compraventa ");
        p.append("de inmuebles en Colombia. Redacta el texto completo del siguiente otrosi en espanol, con ");
        p.append("lenguaje juridico formal: identifica el contrato que se modifica, expon el motivo y la ");
        p.append("justificacion del cambio, y desarrolla las clausulas modificadas y las nuevas condiciones ");
        p.append("acordadas. Cierra dejando explicito que las demas clausulas de la promesa original permanecen ");
        p.append("vigentes sin modificacion. No inventes datos que no esten en esta lista: donde falte un dato ");
        p.append("obligatorio, usa el marcador exacto [DATO FALTANTE] en su lugar. Escribe en prosa clara, ");
        p.append("profesional y bien organizada. No uses formato Markdown: nada de asteriscos (*), dobles ");
        p.append("asteriscos (**) ni numerales (#); el resultado debe ser texto plano listo para un documento ");
        p.append("legal, sin simbolos de marcado.\n\n");

        p.append("DATOS DEL OTROSI\n");
        p.append("Numero de otrosi: ").append(otrosi.getNumeroOtrosi()).append("\n");
        p.append("Fecha de elaboracion: ").append(otrosi.getFechaElaboracion()).append("\n");
        p.append("Contrato que se modifica: Promesa de compraventa ").append(promesa.getNumeroContrato())
                .append(" (negocio ").append(negociacion.getNumeroNegocio())
                .append(", elaborada el ").append(promesa.getFechaElaboracion()).append(")\n");

        p.append("\nMOTIVO Y JUSTIFICACION\n");
        p.append("Motivo: ").append(otrosi.getMotivo()).append("\n");
        if (otrosi.getOtroMotivoTexto() != null && !otrosi.getOtroMotivoTexto().isBlank()) {
            p.append("Detalle del motivo: ").append(otrosi.getOtroMotivoTexto()).append("\n");
        }
        if (otrosi.getJustificacion() != null) {
            p.append("Justificacion: ").append(otrosi.getJustificacion()).append("\n");
        }

        p.append("\nNUEVAS CONDICIONES ACORDADAS\n");
        boolean tieneCambios = false;
        if (otrosi.getNuevoPrecio() != null) {
            p.append("Nuevo precio: ").append(otrosi.getNuevoPrecio()).append(" COP\n");
            tieneCambios = true;
        }
        if (otrosi.getNuevaFechaEscritura() != null) {
            p.append("Nueva fecha de firma de escritura: ").append(otrosi.getNuevaFechaEscritura()).append("\n");
            tieneCambios = true;
        }
        if (otrosi.getNuevaFechaEntrega() != null) {
            p.append("Nueva fecha de entrega: ").append(otrosi.getNuevaFechaEntrega()).append("\n");
            tieneCambios = true;
        }
        if (otrosi.getNuevaDistribucionGastos() != null && !otrosi.getNuevaDistribucionGastos().isBlank()) {
            p.append("Nueva distribucion de gastos: ").append(otrosi.getNuevaDistribucionGastos()).append("\n");
            tieneCambios = true;
        }
        if (otrosi.getNuevosBienesIncluidos() != null && !otrosi.getNuevosBienesIncluidos().isBlank()) {
            p.append("Nuevos bienes incluidos: ").append(otrosi.getNuevosBienesIncluidos()).append("\n");
            tieneCambios = true;
        }
        if (otrosi.getNuevasCondicionesEspeciales() != null && !otrosi.getNuevasCondicionesEspeciales().isBlank()) {
            p.append("Nuevas condiciones especiales: ").append(otrosi.getNuevasCondicionesEspeciales()).append("\n");
            tieneCambios = true;
        }
        if (!tieneCambios) {
            p.append("(No se registraron cambios especificos; redacta el otrosi de forma general segun el motivo indicado)\n");
        }

        if (otrosi.getObservaciones() != null && !otrosi.getObservaciones().isBlank()) {
            p.append("\nObservaciones adicionales: ").append(otrosi.getObservaciones()).append("\n");
        }

        p.append("\nCLAUSULAS DE LA PROMESA ORIGINAL QUE SE MODIFICAN\n");
        if (clausulas.isEmpty()) {
            p.append("(No se especificaron clausulas puntuales; redacta el otrosi refiriendote a las condiciones descritas arriba)\n");
        } else {
            clausulas.forEach(c -> p.append("- ").append(c).append("\n"));
        }

        if (instruccionesAdicionales != null && !instruccionesAdicionales.isBlank()) {
            p.append("\nINSTRUCCIONES ADICIONALES DEL USUARIO (ten en cuenta esto al redactar)\n");
            p.append(instruccionesAdicionales.trim()).append("\n");
        }

        return p.toString();
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
                .createdAt(otrosi.getCreatedAt())
                .build();
    }
}
