package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.documentacion.dto.HallazgoRevisionIA;
import com.felipeparodi.crmbackend.documentacion.dto.HallazgoRevisionResponse;
import com.felipeparodi.crmbackend.documentacion.dto.RevisionActaEntregaIA;
import com.felipeparodi.crmbackend.documentacion.dto.RevisionActaEntregaResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaAcceso;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaCondicion;
import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaPazYSalvo;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaAccesoRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaCondicionRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaPazYSalvoRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ActaEntregaRepository;
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
public class RevisionActaEntregaIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final ActaEntregaRepository actaEntregaRepository;
    private final ActaEntregaCondicionRepository actaEntregaCondicionRepository;
    private final ActaEntregaAccesoRepository actaEntregaAccesoRepository;
    private final ActaEntregaPazYSalvoRepository actaEntregaPazYSalvoRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;

    public RevisionActaEntregaIAService(AnthropicClient anthropicClient,
                                         ActaEntregaRepository actaEntregaRepository,
                                         ActaEntregaCondicionRepository actaEntregaCondicionRepository,
                                         ActaEntregaAccesoRepository actaEntregaAccesoRepository,
                                         ActaEntregaPazYSalvoRepository actaEntregaPazYSalvoRepository,
                                         HistorialNegociacionRepository historialNegociacionRepository) {
        this.anthropicClient = anthropicClient;
        this.actaEntregaRepository = actaEntregaRepository;
        this.actaEntregaCondicionRepository = actaEntregaCondicionRepository;
        this.actaEntregaAccesoRepository = actaEntregaAccesoRepository;
        this.actaEntregaPazYSalvoRepository = actaEntregaPazYSalvoRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
    }

    public RevisionActaEntregaResponse revisar(UUID actaEntregaId) {
        ActaEntrega acta = actaEntregaRepository.findById(actaEntregaId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Acta de entrega no encontrada con id: " + actaEntregaId));

        String prompt = construirPrompt(acta);

        StructuredMessageCreateParams<RevisionActaEntregaIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(3000L)
                .outputConfig(RevisionActaEntregaIA.class)
                .addUserMessage(prompt)
                .build();

        RevisionActaEntregaIA resultado;
        try {
            resultado = anthropicClient.messages().create(params).content().stream()
                    .flatMap(block -> block.text().stream())
                    .findFirst()
                    .map(typed -> typed.text())
                    .orElseThrow(() -> new IllegalStateException(
                            "La IA no genero una revision valida para el acta " + actaEntregaId));
        } catch (com.anthropic.errors.AnthropicException ex) {
            throw new ServicioIANoDisponibleException(
                    "No se pudo revisar el acta de entrega: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        registrarHistorial(acta.getNegociacion(), acta, resultado);

        return mapearAResponse(acta, resultado);
    }

    private String construirPrompt(ActaEntrega acta) {
        Negociacion negociacion = acta.getNegociacion();

        List<ActaEntregaCondicion> condiciones = actaEntregaCondicionRepository.findByActaEntregaId(acta.getId());
        List<ActaEntregaAcceso> accesos = actaEntregaAccesoRepository.findByActaEntregaId(acta.getId());
        List<ActaEntregaPazYSalvo> pazYSalvos = actaEntregaPazYSalvoRepository.findByActaEntregaId(acta.getId());

        StringBuilder p = new StringBuilder();
        p.append("Eres un asistente que revisa actas de entrega de inmuebles en Colombia antes de que se firmen. ");
        p.append("Detecta inconsistencias o contradicciones en la informacion, por ejemplo: la declaracion de ");
        p.append("conformidad no coincide con danos o reparaciones pendientes reportados; el vendedor declara ");
        p.append("entrega conforme pero hay paz y salvos pendientes o accesos sin entregar; la entrega se esta ");
        p.append("registrando sin que la escritura o el pago de la negociacion esten confirmados; o la fecha de ");
        p.append("entrega es anterior a la fecha de elaboracion del acta. No inventes datos que no esten en esta ");
        p.append("lista.\n\n");

        p.append("NEGOCIACION\n");
        p.append("Numero de negocio: ").append(negociacion.getNumeroNegocio()).append("\n");
        p.append("Escritura firmada: ").append(negociacion.isEscrituraFirmada()).append("\n");
        p.append("Pago recibido: ").append(negociacion.isPagoRecibido()).append("\n");

        p.append("\nACTA DE ENTREGA\n");
        p.append("Numero de acta: ").append(acta.getNumeroActa()).append("\n");
        p.append("Fecha de elaboracion: ").append(acta.getFechaElaboracion()).append("\n");
        p.append("Fecha y hora de entrega: ").append(acta.getFechaHoraEntrega()).append("\n");
        p.append("Declaracion del comprador: ").append(acta.getDeclaracionComprador()).append("\n");
        p.append("Vendedor entrega conforme: ").append(acta.isVendedorEntregaConforme()).append("\n");
        if (acta.getDanosExistentes() != null && !acta.getDanosExistentes().isBlank()) {
            p.append("Danos existentes reportados: ").append(acta.getDanosExistentes()).append("\n");
        }
        if (acta.getReparacionesPendientes() != null && !acta.getReparacionesPendientes().isBlank()) {
            p.append("Reparaciones pendientes: ").append(acta.getReparacionesPendientes()).append("\n");
        }
        if (acta.getCompromisosAsumidos() != null && !acta.getCompromisosAsumidos().isBlank()) {
            p.append("Compromisos asumidos: ").append(acta.getCompromisosAsumidos()).append("\n");
        }
        if (acta.getObservaciones() != null && !acta.getObservaciones().isBlank()) {
            p.append("Observaciones: ").append(acta.getObservaciones()).append("\n");
        }

        p.append("\nCONDICIONES DE ENTREGA MARCADAS COMO CUMPLIDAS\n");
        appendNombres(p, condiciones.stream()
                .filter(ActaEntregaCondicion::isCumple)
                .map(c -> c.getCondicionEntrega().getNombre())
                .collect(Collectors.toList()));

        p.append("\nACCESOS Y LLAVES ENTREGADOS\n");
        appendNombres(p, accesos.stream()
                .filter(ActaEntregaAcceso::isEntregado)
                .map(a -> a.getAccesoEntrega().getNombre())
                .collect(Collectors.toList()));

        p.append("\nPAZ Y SALVOS AL DIA\n");
        appendNombres(p, pazYSalvos.stream()
                .filter(ActaEntregaPazYSalvo::isAlDia)
                .map(pys -> pys.getPazYSalvoTipo().getNombre())
                .collect(Collectors.toList()));

        p.append("\nPAZ Y SALVOS PENDIENTES\n");
        appendNombres(p, pazYSalvos.stream()
                .filter(pys -> !pys.isAlDia())
                .map(pys -> pys.getPazYSalvoTipo().getNombre())
                .collect(Collectors.toList()));

        return p.toString();
    }

    private void appendNombres(StringBuilder p, List<String> nombres) {
        if (nombres.isEmpty()) {
            p.append("(ninguno)\n");
        } else {
            nombres.forEach(n -> p.append("- ").append(n).append("\n"));
        }
    }

    private void registrarHistorial(Negociacion negociacion, ActaEntrega acta, RevisionActaEntregaIA resultado) {
        String evento = "Revision IA del acta " + acta.getNumeroActa() + ": "
                + (resultado.listaParaFirmar() ? "lista para firmar" : "inconsistencias detectadas")
                + " (" + resultado.hallazgos().size() + " hallazgo(s))";

        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private RevisionActaEntregaResponse mapearAResponse(ActaEntrega acta, RevisionActaEntregaIA resultado) {
        List<HallazgoRevisionResponse> hallazgos = resultado.hallazgos().stream()
                .map(this::mapearHallazgo)
                .collect(Collectors.toList());

        return RevisionActaEntregaResponse.builder()
                .actaEntregaId(acta.getId())
                .numeroActa(acta.getNumeroActa())
                .resumenGeneral(resultado.resumenGeneral())
                .listaParaFirmar(resultado.listaParaFirmar())
                .hallazgos(hallazgos)
                .fechaRevision(LocalDateTime.now())
                .build();
    }

    private HallazgoRevisionResponse mapearHallazgo(HallazgoRevisionIA hallazgo) {
        return HallazgoRevisionResponse.builder()
                .descripcion(hallazgo.descripcion())
                .severidad(hallazgo.severidad())
                .build();
    }
}
