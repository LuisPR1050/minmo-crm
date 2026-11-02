package com.felipeparodi.crmbackend.atencioncliente.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesIA;
import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AnalisisObjecionesIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final ClienteRepository clienteRepository;
    private final VisitaClienteRepository visitaClienteRepository;
    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository;

    public AnalisisObjecionesIAService(AnthropicClient anthropicClient,
                                        ClienteRepository clienteRepository,
                                        VisitaClienteRepository visitaClienteRepository,
                                        ResultadoVisitaRepository resultadoVisitaRepository,
                                        ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository) {
        this.anthropicClient = anthropicClient;
        this.clienteRepository = clienteRepository;
        this.visitaClienteRepository = visitaClienteRepository;
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.resultadoVisitaObjecionRepository = resultadoVisitaObjecionRepository;
    }

    public AnalisisObjecionesResponse analizar(UUID clienteId) {
        Cliente cliente = clienteRepository.findById(clienteId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + clienteId));

        List<VisitaCliente> visitas = visitaClienteRepository.findByClienteId(clienteId);

        List<PuntoAnalisis> puntos = new ArrayList<>();
        for (VisitaCliente visita : visitas) {
            resultadoVisitaRepository.findByVisitaId(visita.getId()).ifPresent(resultado -> {
                List<String> objeciones = resultadoVisitaObjecionRepository.findByResultadoVisitaId(resultado.getId())
                        .stream()
                        .map(ro -> ro.getObjecion().getNombre())
                        .toList();

                boolean tieneObservaciones = resultado.getObservacionesAsesor() != null
                        && !resultado.getObservacionesAsesor().isBlank();

                if (!objeciones.isEmpty() || tieneObservaciones) {
                    puntos.add(new PuntoAnalisis(visita, resultado, objeciones));
                }
            });
        }

        if (puntos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Este cliente no tiene objeciones ni observaciones registradas en sus visitas todavia");
        }

        String prompt = construirPrompt(cliente, puntos);

        StructuredMessageCreateParams<AnalisisObjecionesIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1024L)
                .outputConfig(AnalisisObjecionesIA.class)
                .addUserMessage(prompt)
                .build();

        AnalisisObjecionesIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero un analisis de objeciones para el cliente " + clienteId));

        return AnalisisObjecionesResponse.builder()
                .clienteId(cliente.getId())
                .resumen(resultado.resumen())
                .recomendaciones(resultado.recomendaciones())
                .visitasAnalizadas(puntos.size())
                .build();
    }

    private String construirPrompt(Cliente cliente, List<PuntoAnalisis> puntos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un coach de ventas inmobiliarias en Colombia. Analiza las objeciones y observaciones ");
        p.append("registradas por el asesor en las visitas de este cliente, identifica patrones y sugiere como ");
        p.append("abordarlas para avanzar el cierre del negocio. No inventes objeciones que no esten en esta lista.\n\n");

        p.append("CLIENTE: ").append(cliente.getNombre()).append("\n\n");
        p.append("VISITAS CON OBJECIONES U OBSERVACIONES (").append(puntos.size()).append(")\n");

        for (PuntoAnalisis punto : puntos) {
            p.append("- Visita ").append(punto.visita().getCodigoVisita());
            if (punto.visita().getFechaVisita() != null) {
                p.append(" (").append(punto.visita().getFechaVisita()).append(")");
            }
            p.append(":\n");
            if (punto.resultado().getNivelInteres() != null) {
                p.append("  Nivel de interes: ").append(punto.resultado().getNivelInteres()).append("\n");
            }
            if (punto.resultado().getIntencionCompra() != null) {
                p.append("  Intencion de compra: ").append(punto.resultado().getIntencionCompra()).append("\n");
            }
            if (!punto.objeciones().isEmpty()) {
                p.append("  Objeciones: ").append(String.join(", ", punto.objeciones())).append("\n");
            }
            if (punto.resultado().getObservacionesAsesor() != null && !punto.resultado().getObservacionesAsesor().isBlank()) {
                p.append("  Observaciones del asesor: ").append(punto.resultado().getObservacionesAsesor()).append("\n");
            }
        }

        return p.toString();
    }

    private record PuntoAnalisis(VisitaCliente visita, ResultadoVisita resultado, List<String> objeciones) {
    }
}
