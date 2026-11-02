package com.felipeparodi.crmbackend.atencioncliente.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesIA;
import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesInmuebleResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import com.felipeparodi.crmbackend.captacion.service.InmuebleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class AnalisisObjecionesInmuebleIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final InmuebleService inmuebleService;
    private final VisitaClienteRepository visitaClienteRepository;
    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository;

    public AnalisisObjecionesInmuebleIAService(AnthropicClient anthropicClient,
                                                InmuebleService inmuebleService,
                                                VisitaClienteRepository visitaClienteRepository,
                                                ResultadoVisitaRepository resultadoVisitaRepository,
                                                ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository) {
        this.anthropicClient = anthropicClient;
        this.inmuebleService = inmuebleService;
        this.visitaClienteRepository = visitaClienteRepository;
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.resultadoVisitaObjecionRepository = resultadoVisitaObjecionRepository;
    }

    public AnalisisObjecionesInmuebleResponse analizar(UUID inmuebleId) {
        InmuebleResponse inmueble = inmuebleService.buscarPorId(inmuebleId);

        List<VisitaCliente> visitas = visitaClienteRepository.findByInmuebleId(inmuebleId);
        if (visitas.isEmpty()) {
            throw new IllegalArgumentException("Este inmueble todavia no tiene visitas registradas");
        }

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
                    "Este inmueble tiene " + visitas.size()
                            + " visita(s) pero ninguna registro objeciones u observaciones todavia");
        }

        String prompt = construirPrompt(inmueble, visitas.size(), puntos);

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
                        "La IA no genero un analisis de objeciones para el inmueble " + inmuebleId));

        return AnalisisObjecionesInmuebleResponse.builder()
                .inmuebleId(inmuebleId)
                .resumen(resultado.resumen())
                .recomendaciones(resultado.recomendaciones())
                .totalVisitas(visitas.size())
                .visitasAnalizadas(puntos.size())
                .build();
    }

    private String construirPrompt(InmuebleResponse inmueble, int totalVisitas, List<PuntoAnalisis> puntos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un coach de ventas inmobiliarias en Colombia. Esta propiedad ha recibido ").append(totalVisitas);
        p.append(" visita(s) de distintos clientes potenciales. Analiza los patrones en las objeciones y ");
        p.append("observaciones registradas por los asesores en esas visitas, identifica que es lo que mas esta ");
        p.append("frenando el cierre de esta propiedad especifica, y sugiere acciones concretas (por ejemplo ajustar ");
        p.append("precio, mejorar presentacion, resolver un problema recurrente, etc). No inventes objeciones que no ");
        p.append("esten en esta lista.\n\n");

        p.append("PROPIEDAD: ").append(inmueble.getCodigo()).append(" (").append(inmueble.getTipoPropiedad()).append(")");
        if (inmueble.getZona() != null) {
            p.append(", zona ").append(inmueble.getZona());
        }
        p.append("\n\n");

        p.append("VISITAS CON OBJECIONES U OBSERVACIONES (").append(puntos.size()).append(" de ").append(totalVisitas).append(")\n");

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
