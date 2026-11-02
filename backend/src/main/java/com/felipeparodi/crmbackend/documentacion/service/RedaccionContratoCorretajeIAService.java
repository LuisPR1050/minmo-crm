package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.OutputConfig;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.documentacion.dto.ContratoCorretajeResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeAutorizacion;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeClausula;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeAutorizacionRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeClausulaRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.HistorialContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.entity.HistorialContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.util.LimpiadorMarkdownIA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class RedaccionContratoCorretajeIAService {

    private static final String MODELO = "claude-opus-5";

    private static final String DISCLAIMER = "\n\n---\nBORRADOR GENERADO AUTOMATICAMENTE POR IA. "
            + "Requiere revision de un abogado antes de ser firmado. Los campos marcados como "
            + "[DATO FALTANTE] deben completarse manualmente.";

    private static final String SUBCARPETA_PDF = "contratos-corretaje-pdf";

    private final AnthropicClient anthropicClient;
    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository;
    private final ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository;
    private final HistorialContratoCorretajeRepository historialContratoCorretajeRepository;
    private final PdfDocumentoService pdfDocumentoService;

    public RedaccionContratoCorretajeIAService(AnthropicClient anthropicClient,
                                                ContratoCorretajeRepository contratoCorretajeRepository,
                                                ContratoCorretajeClausulaRepository contratoCorretajeClausulaRepository,
                                                ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository,
                                                HistorialContratoCorretajeRepository historialContratoCorretajeRepository,
                                                PdfDocumentoService pdfDocumentoService) {
        this.anthropicClient = anthropicClient;
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.contratoCorretajeClausulaRepository = contratoCorretajeClausulaRepository;
        this.contratoCorretajeAutorizacionRepository = contratoCorretajeAutorizacionRepository;
        this.historialContratoCorretajeRepository = historialContratoCorretajeRepository;
        this.pdfDocumentoService = pdfDocumentoService;
    }

    public ContratoCorretajeResponse generarTexto(UUID contratoCorretajeId, String instruccionesAdicionales) {
        ContratoCorretaje contrato = contratoCorretajeRepository.findById(contratoCorretajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato de corretaje no encontrado con id: " + contratoCorretajeId));

        String prompt = construirPrompt(contrato, instruccionesAdicionales);

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
                    "No se pudo redactar el contrato de corretaje: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        String textoGenerado = response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(block -> block.text())
                .collect(Collectors.joining("\n"));

        if (textoGenerado.isBlank()) {
            throw new IllegalStateException(
                    "La IA no genero texto para el contrato " + contratoCorretajeId);
        }

        textoGenerado = LimpiadorMarkdownIA.limpiar(textoGenerado);

        contrato.setTextoContrato(textoGenerado + DISCLAIMER);
        contrato.setFechaGeneracionTexto(LocalDateTime.now());
        contrato.setPdfUrl(pdfDocumentoService.generarYGuardar(SUBCARPETA_PDF,
                "Contrato de corretaje " + contrato.getNumeroContrato(),
                contrato.getTextoContrato(), contrato.getPdfUrl()));

        ContratoCorretaje guardado = contratoCorretajeRepository.save(contrato);

        registrarHistorial(guardado, "Texto del contrato generado por IA");

        return mapearAResponse(guardado);
    }

    private String construirPrompt(ContratoCorretaje contrato, String instruccionesAdicionales) {
        Inmueble inmueble = contrato.getInmueble();
        Propietario propietario = contrato.getPropietario();

        List<String> clausulas = contratoCorretajeClausulaRepository
                .findByContratoCorretajeId(contrato.getId())
                .stream()
                .filter(ContratoCorretajeClausula::isIncluida)
                .map(c -> c.getClausulaCorretaje().getNombre())
                .collect(Collectors.toList());

        List<String> autorizaciones = contratoCorretajeAutorizacionRepository
                .findByContratoCorretajeId(contrato.getId())
                .stream()
                .filter(ContratoCorretajeAutorizacion::isAutorizada)
                .map(a -> a.getAutorizacionPropietario().getNombre())
                .collect(Collectors.toList());

        StringBuilder p = new StringBuilder();
        p.append("Eres un asistente legal que redacta contratos de corretaje inmobiliario en Colombia. ");
        p.append("Redacta el texto completo del siguiente contrato en espanol, con lenguaje juridico formal, ");
        p.append("numerando las clausulas. Incluye UNICAMENTE las clausulas listadas abajo, desarrollando ");
        p.append("cada una con el contenido juridico habitual para ese tipo de clausula en un contrato de ");
        p.append("corretaje inmobiliario colombiano. No inventes datos que no esten en esta lista: donde falte ");
        p.append("un dato obligatorio (por ejemplo, numero de cedula de una de las partes), usa el marcador ");
        p.append("exacto [DATO FALTANTE] en su lugar. Escribe en prosa clara, profesional y bien organizada. ");
        p.append("No uses formato Markdown: nada de asteriscos (*), dobles asteriscos (**) ni numerales (#); ");
        p.append("el resultado debe ser texto plano listo para un documento legal, sin simbolos de marcado.\n\n");

        p.append("DATOS DEL CONTRATO\n");
        p.append("Numero de contrato: ").append(contrato.getNumeroContrato()).append("\n");
        p.append("Fecha de elaboracion: ").append(contrato.getFechaElaboracion()).append("\n");
        p.append("Ciudad: ").append(contrato.getCiudad()).append("\n");
        p.append("Tipo de contrato: ").append(contrato.getTipoContrato()).append("\n");
        if (contrato.getOpcionFirma() != null) {
            p.append("Opcion de firma: ").append(contrato.getOpcionFirma()).append("\n");
        }

        p.append("\nDATOS DEL PROPIETARIO (mandante)\n");
        p.append("Nombre: ").append(propietario.getNombre()).append("\n");
        if (propietario.getTelefonoPrincipal() != null) {
            p.append("Telefono: ").append(propietario.getTelefonoPrincipal()).append("\n");
        }
        if (propietario.getCorreoElectronico() != null) {
            p.append("Correo: ").append(propietario.getCorreoElectronico()).append("\n");
        }
        p.append("Numero de identificacion: [DATO FALTANTE]\n");

        p.append("\nDATOS DEL INMUEBLE\n");
        p.append("Codigo interno: ").append(inmueble.getCodigo()).append("\n");
        p.append("Tipo de propiedad: ").append(inmueble.getTipoPropiedad()).append("\n");
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getZona() != null) {
            p.append("Zona: ").append(inmueble.getZona()).append("\n");
        }
        if (inmueble.getDireccionExacta() != null) {
            p.append("Direccion: ").append(inmueble.getDireccionExacta()).append("\n");
        } else {
            p.append("Direccion: [DATO FALTANTE]\n");
        }
        if (inmueble.getMatriculaInmobiliaria() != null) {
            p.append("Matricula inmobiliaria: ").append(inmueble.getMatriculaInmobiliaria()).append("\n");
        } else {
            p.append("Matricula inmobiliaria: [DATO FALTANTE]\n");
        }

        p.append("\nCONDICIONES DE CORRETAJE\n");
        if (contrato.getPrecioAutorizadoVenta() != null) {
            p.append("Precio autorizado de venta: ").append(contrato.getPrecioAutorizadoVenta()).append(" COP\n");
        }
        if (contrato.getPrecioMinimoAutorizado() != null) {
            p.append("Precio minimo autorizado: ").append(contrato.getPrecioMinimoAutorizado()).append(" COP\n");
        }
        if (contrato.getPorcentajeComision() != null) {
            p.append("Comision (porcentaje): ").append(contrato.getPorcentajeComision()).append("%\n");
        }
        if (contrato.getValorFijoComision() != null) {
            p.append("Comision (valor fijo): ").append(contrato.getValorFijoComision()).append(" COP\n");
        }
        if (contrato.getSobreprecioComision() != null) {
            p.append("Comision (sobreprecio): ").append(contrato.getSobreprecioComision()).append(" COP\n");
        }
        if (contrato.getFormaPagoComision() != null) {
            p.append("Forma de pago de la comision: ").append(contrato.getFormaPagoComision()).append("\n");
        }
        if (contrato.getFechaVigenciaHasta() != null) {
            p.append("Vigencia del contrato hasta: ").append(contrato.getFechaVigenciaHasta()).append("\n");
        }

        p.append("\nCLAUSULAS A INCLUIR (en este orden)\n");
        if (clausulas.isEmpty()) {
            p.append("(No se han seleccionado clausulas todavia; redacta solo el encabezado y los datos de las partes)\n");
        } else {
            clausulas.forEach(c -> p.append("- ").append(c).append("\n"));
        }

        if (!autorizaciones.isEmpty()) {
            p.append("\nAUTORIZACIONES OTORGADAS POR EL PROPIETARIO\n");
            autorizaciones.forEach(a -> p.append("- ").append(a).append("\n"));
        }

        if (instruccionesAdicionales != null && !instruccionesAdicionales.isBlank()) {
            p.append("\nINSTRUCCIONES ADICIONALES DEL USUARIO (ten en cuenta esto al redactar)\n");
            p.append(instruccionesAdicionales.trim()).append("\n");
        }

        return p.toString();
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
                .createdAt(contrato.getCreatedAt())
                .build();
    }
}
