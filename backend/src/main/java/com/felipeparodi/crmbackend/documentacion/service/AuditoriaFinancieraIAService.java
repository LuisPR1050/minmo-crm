package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.documentacion.dto.AuditoriaFinancieraIA;
import com.felipeparodi.crmbackend.documentacion.dto.AuditoriaFinancieraResponse;
import com.felipeparodi.crmbackend.documentacion.dto.HallazgoRevisionIA;
import com.felipeparodi.crmbackend.documentacion.dto.HallazgoRevisionResponse;
import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import com.felipeparodi.crmbackend.documentacion.repository.ReciboPagoRepository;
import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
import com.felipeparodi.crmbackend.negociacion.repository.HistorialNegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class AuditoriaFinancieraIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final NegociacionRepository negociacionRepository;
    private final PlanDePagoRepository planDePagoRepository;
    private final ReciboPagoRepository reciboPagoRepository;
    private final HistorialNegociacionRepository historialNegociacionRepository;

    public AuditoriaFinancieraIAService(AnthropicClient anthropicClient,
                                         NegociacionRepository negociacionRepository,
                                         PlanDePagoRepository planDePagoRepository,
                                         ReciboPagoRepository reciboPagoRepository,
                                         HistorialNegociacionRepository historialNegociacionRepository) {
        this.anthropicClient = anthropicClient;
        this.negociacionRepository = negociacionRepository;
        this.planDePagoRepository = planDePagoRepository;
        this.reciboPagoRepository = reciboPagoRepository;
        this.historialNegociacionRepository = historialNegociacionRepository;
    }

    public AuditoriaFinancieraResponse auditar(UUID negociacionId) {
        Negociacion negociacion = negociacionRepository.findById(negociacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Negociacion no encontrada con id: " + negociacionId));

        List<PlanDePago> plan = planDePagoRepository.findByNegociacionId(negociacionId);
        List<ReciboPago> recibos = reciboPagoRepository.findByNegociacionId(negociacionId);

        String prompt = construirPrompt(negociacion, plan, recibos);

        StructuredMessageCreateParams<AuditoriaFinancieraIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(3000L)
                .outputConfig(AuditoriaFinancieraIA.class)
                .addUserMessage(prompt)
                .build();

        AuditoriaFinancieraIA resultado;
        try {
            resultado = anthropicClient.messages().create(params).content().stream()
                    .flatMap(block -> block.text().stream())
                    .findFirst()
                    .map(typed -> typed.text())
                    .orElseThrow(() -> new IllegalStateException(
                            "La IA no genero una auditoria valida para la negociacion " + negociacionId));
        } catch (com.anthropic.errors.AnthropicException ex) {
            throw new ServicioIANoDisponibleException(
                    "No se pudo generar la auditoria financiera: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        registrarHistorial(negociacion, resultado);

        return mapearAResponse(negociacion, resultado);
    }

    private String construirPrompt(Negociacion negociacion, List<PlanDePago> plan, List<ReciboPago> recibos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un auditor financiero de negociaciones inmobiliarias en Colombia. Revisa si los pagos ");
        p.append("registrados (recibos de pago) coinciden con lo pactado en el plan de pago de la negociacion. ");
        p.append("Senala inconsistencias como: pagos que exceden lo pactado en una cuota, cuotas marcadas como ");
        p.append("pagadas sin un recibo que las respalde, recibos sin cuota asociada del plan, forma de pago ");
        p.append("distinta a la pactada, o el total recibido no coincide con el precio final acordado si ya se ");
        p.append("recibieron todos los pagos esperados. No inventes datos que no esten en esta lista.\n\n");

        p.append("NEGOCIACION\n");
        p.append("Numero de negocio: ").append(negociacion.getNumeroNegocio()).append("\n");
        p.append("Estado: ").append(negociacion.getEstado()).append("\n");
        if (negociacion.getPrecioFinalAcordado() != null) {
            p.append("Precio final acordado: ").append(negociacion.getPrecioFinalAcordado()).append(" COP\n");
        }

        p.append("\nPLAN DE PAGO PACTADO\n");
        if (plan.isEmpty()) {
            p.append("(No hay plan de pago registrado)\n");
        } else {
            for (PlanDePago cuota : plan) {
                p.append("- Recurso: ").append(cuota.getTipoRecurso());
                if (cuota.getNumeroCuota() != null) {
                    p.append(", cuota #").append(cuota.getNumeroCuota());
                }
                if (cuota.getValor() != null) {
                    p.append(", valor pactado: ").append(cuota.getValor()).append(" COP");
                }
                if (cuota.getPorcentaje() != null) {
                    p.append(" (").append(cuota.getPorcentaje()).append("%)");
                }
                if (cuota.getMomentoPago() != null) {
                    p.append(", momento: ").append(cuota.getMomentoPago());
                }
                if (cuota.getMetodoPago() != null) {
                    p.append(", metodo pactado: ").append(cuota.getMetodoPago());
                }
                if (cuota.getFechaEsperada() != null) {
                    p.append(", fecha esperada: ").append(cuota.getFechaEsperada());
                }
                p.append(", marcada como pagada: ").append(cuota.isPagada()).append("\n");
            }
        }

        p.append("\nRECIBOS DE PAGO REGISTRADOS\n");
        if (recibos.isEmpty()) {
            p.append("(No se ha registrado ningun recibo de pago)\n");
        } else {
            for (ReciboPago recibo : recibos) {
                p.append("- Recibo ").append(recibo.getNumeroRecibo())
                        .append(", concepto: ").append(recibo.getConceptoPago());
                if (recibo.getNumeroCuota() != null) {
                    p.append(", cuota #").append(recibo.getNumeroCuota());
                }
                p.append(", valor recibido: ").append(recibo.getValorRecibido()).append(" COP");
                if (recibo.getFormaPago() != null) {
                    p.append(", forma de pago: ").append(recibo.getFormaPago());
                }
                p.append(", fecha: ").append(recibo.getFechaElaboracion());
                p.append(", ligado a cuota del plan: ").append(recibo.getPlanDePago() != null).append("\n");
            }
        }

        return p.toString();
    }

    private void registrarHistorial(Negociacion negociacion, AuditoriaFinancieraIA resultado) {
        String evento = "Auditoria financiera IA: " + (resultado.consistente() ? "consistente" : "inconsistencias detectadas")
                + " (" + resultado.hallazgos().size() + " hallazgo(s))";

        HistorialNegociacion historial = HistorialNegociacion.builder()
                .negociacion(negociacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialNegociacionRepository.save(historial);
    }

    private AuditoriaFinancieraResponse mapearAResponse(Negociacion negociacion, AuditoriaFinancieraIA resultado) {
        List<HallazgoRevisionResponse> hallazgos = resultado.hallazgos().stream()
                .map(this::mapearHallazgo)
                .collect(Collectors.toList());

        return AuditoriaFinancieraResponse.builder()
                .negociacionId(negociacion.getId())
                .numeroNegocio(negociacion.getNumeroNegocio())
                .resumenGeneral(resultado.resumenGeneral())
                .consistente(resultado.consistente())
                .hallazgos(hallazgos)
                .fechaAuditoria(LocalDateTime.now())
                .build();
    }

    private HallazgoRevisionResponse mapearHallazgo(HallazgoRevisionIA hallazgo) {
        return HallazgoRevisionResponse.builder()
                .descripcion(hallazgo.descripcion())
                .severidad(hallazgo.severidad())
                .build();
    }
}
