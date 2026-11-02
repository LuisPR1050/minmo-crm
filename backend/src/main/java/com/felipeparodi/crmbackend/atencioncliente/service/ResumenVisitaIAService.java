package com.felipeparodi.crmbackend.atencioncliente.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResumenVisitaIA;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResumenVisitaResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class ResumenVisitaIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository;

    public ResumenVisitaIAService(AnthropicClient anthropicClient,
                                   ResultadoVisitaRepository resultadoVisitaRepository,
                                   ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository) {
        this.anthropicClient = anthropicClient;
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.resultadoVisitaObjecionRepository = resultadoVisitaObjecionRepository;
    }

    public ResumenVisitaResponse generarResumen(UUID resultadoVisitaId) {
        ResultadoVisita resultado = resultadoVisitaRepository.findById(resultadoVisitaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Resultado de visita no encontrado con id: " + resultadoVisitaId));

        VisitaCliente visita = resultado.getVisita();
        Inmueble inmueble = visita.getInmueble();

        List<String> objeciones = resultadoVisitaObjecionRepository.findByResultadoVisitaId(resultado.getId())
                .stream()
                .map(rvo -> rvo.getObjecion().getNombre())
                .toList();

        String prompt = construirPrompt(resultado, visita, inmueble, objeciones);

        StructuredMessageCreateParams<ResumenVisitaIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(768L)
                .outputConfig(ResumenVisitaIA.class)
                .addUserMessage(prompt)
                .build();

        ResumenVisitaIA generado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero un resumen para el resultado de visita " + resultadoVisitaId));

        resultado.setResumenGeneradoIA(generado.resumen());
        resultadoVisitaRepository.save(resultado);

        return ResumenVisitaResponse.builder()
                .resultadoVisitaId(resultadoVisitaId)
                .resumen(generado.resumen())
                .build();
    }

    private String construirPrompt(ResultadoVisita resultado, VisitaCliente visita, Inmueble inmueble,
                                    List<String> objeciones) {
        StringBuilder p = new StringBuilder();
        p.append("Redacta un resumen profesional de esta visita inmobiliaria en Colombia, pensado para compartir ");
        p.append("con el propietario de la propiedad. Se claro, breve y neutral. No inventes datos que no esten ");
        p.append("en esta informacion.\n\n");

        p.append("PROPIEDAD: ").append(inmueble.getCodigo()).append(" (").append(inmueble.getTipoPropiedad()).append(")\n");
        if (visita.getFechaVisita() != null) {
            p.append("Fecha de la visita: ").append(visita.getFechaVisita()).append("\n");
        }
        if (visita.getTipoVisita() != null) {
            p.append("Tipo de visita: ").append(visita.getTipoVisita()).append("\n");
        }
        if (resultado.getNivelInteres() != null) {
            p.append("Nivel de interes del cliente: ").append(resultado.getNivelInteres()).append("\n");
        }
        if (resultado.getIntencionCompra() != null) {
            p.append("Intencion de compra: ").append(resultado.getIntencionCompra()).append("\n");
        }
        if (!objeciones.isEmpty()) {
            p.append("Objeciones detectadas: ").append(String.join(", ", objeciones)).append("\n");
        }
        if (resultado.getObservacionesAsesor() != null && !resultado.getObservacionesAsesor().isBlank()) {
            p.append("Observaciones del asesor: ").append(resultado.getObservacionesAsesor()).append("\n");
        }
        if (resultado.getProximaAccion() != null) {
            p.append("Proxima accion planeada: ").append(resultado.getProximaAccion()).append("\n");
        }

        return p.toString();
    }
}
