package com.felipeparodi.crmbackend.captacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.captacion.dto.PrecioSugeridoIA;
import com.felipeparodi.crmbackend.captacion.dto.PrecioSugeridoResponse;
import com.felipeparodi.crmbackend.captacion.entity.EstadoCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.HistorialCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.HistorialCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Transactional
public class PrecioSugeridoIAService {

    private static final String MODELO = "claude-opus-5";
    private static final int MINIMO_COMPARABLES = 2;

    private final AnthropicClient anthropicClient;
    private final InmuebleRepository inmuebleRepository;
    private final HistorialCaptacionRepository historialCaptacionRepository;

    public PrecioSugeridoIAService(AnthropicClient anthropicClient,
                                    InmuebleRepository inmuebleRepository,
                                    HistorialCaptacionRepository historialCaptacionRepository) {
        this.anthropicClient = anthropicClient;
        this.inmuebleRepository = inmuebleRepository;
        this.historialCaptacionRepository = historialCaptacionRepository;
    }

    public PrecioSugeridoResponse sugerir(UUID inmuebleId) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        List<Inmueble> comparables = buscarComparables(inmueble);

        if (comparables.size() < MINIMO_COMPARABLES) {
            throw new IllegalArgumentException(
                    "No hay suficientes inmuebles comparables (mismo tipo y zona/ciudad, con precio registrado) "
                            + "para sugerir un precio. Se encontraron " + comparables.size()
                            + ", se necesitan al menos " + MINIMO_COMPARABLES + ".");
        }

        String prompt = construirPrompt(inmueble, comparables);

        StructuredMessageCreateParams<PrecioSugeridoIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1024L)
                .outputConfig(PrecioSugeridoIA.class)
                .addUserMessage(prompt)
                .build();

        PrecioSugeridoIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero una sugerencia de precio para el inmueble " + inmuebleId));

        inmueble.setPrecioSugerido(BigDecimal.valueOf(resultado.precioSugerido()));
        inmuebleRepository.save(inmueble);

        registrarHistorial(inmueble, resultado);

        return PrecioSugeridoResponse.builder()
                .inmuebleId(inmueble.getId())
                .precioSugerido(resultado.precioSugerido())
                .rangoMinimo(resultado.rangoMinimo())
                .rangoMaximo(resultado.rangoMaximo())
                .justificacion(resultado.justificacion())
                .comparablesUsados(comparables.size())
                .build();
    }

    private List<Inmueble> buscarComparables(Inmueble inmueble) {
        return inmuebleRepository.findAll().stream()
                .filter(otro -> !otro.getId().equals(inmueble.getId()))
                .filter(otro -> otro.getEstadoCaptacion() != EstadoCaptacion.DESCARTADO)
                .filter(otro -> Objects.equals(otro.getTipoPropiedad(), inmueble.getTipoPropiedad()))
                .filter(otro -> coincideUbicacion(otro, inmueble))
                .filter(otro -> otro.getExpectativaPrecio() != null || otro.getPrecioMinimoAceptado() != null)
                .toList();
    }

    private boolean coincideUbicacion(Inmueble candidato, Inmueble referencia) {
        if (referencia.getZona() != null) {
            return referencia.getZona().equalsIgnoreCase(candidato.getZona());
        }
        return referencia.getCiudad() != null && referencia.getCiudad().equalsIgnoreCase(candidato.getCiudad());
    }

    private String construirPrompt(Inmueble inmueble, List<Inmueble> comparables) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un asesor inmobiliario experto en Valledupar, Colombia. Sugiere un precio de venta ");
        p.append("competitivo para la siguiente propiedad, basandote UNICAMENTE en los inmuebles comparables ");
        p.append("listados abajo (mismo tipo y zona/ciudad). No inventes datos que no esten en esta informacion.\n\n");

        p.append("PROPIEDAD A VALORAR\n");
        p.append("Tipo: ").append(inmueble.getTipoPropiedad()).append("\n");
        if (inmueble.getZona() != null) {
            p.append("Zona: ").append(inmueble.getZona()).append("\n");
        }
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getAreaTotal() != null) {
            p.append("Area total: ").append(inmueble.getAreaTotal()).append(" m2\n");
        }
        if (inmueble.getAreaConstruida() != null) {
            p.append("Area construida: ").append(inmueble.getAreaConstruida()).append(" m2\n");
        }
        if (inmueble.getNumeroHabitaciones() != null) {
            p.append("Habitaciones: ").append(inmueble.getNumeroHabitaciones()).append("\n");
        }
        if (inmueble.getNumeroBanos() != null) {
            p.append("Banos: ").append(inmueble.getNumeroBanos()).append("\n");
        }
        if (inmueble.getEstratoSocioeconomico() != null) {
            p.append("Estrato: ").append(inmueble.getEstratoSocioeconomico()).append("\n");
        }
        if (inmueble.getEstadoFisico() != null) {
            p.append("Estado fisico: ").append(inmueble.getEstadoFisico()).append("\n");
        }

        p.append("\nCOMPARABLES (").append(comparables.size()).append(")\n");
        for (Inmueble c : comparables) {
            p.append("- ").append(c.getCodigo()).append(": ");
            if (c.getAreaTotal() != null) {
                p.append(c.getAreaTotal()).append(" m2, ");
            }
            if (c.getNumeroHabitaciones() != null) {
                p.append(c.getNumeroHabitaciones()).append(" hab, ");
            }
            if (c.getNumeroBanos() != null) {
                p.append(c.getNumeroBanos()).append(" banos, ");
            }
            if (c.getEstratoSocioeconomico() != null) {
                p.append("estrato ").append(c.getEstratoSocioeconomico()).append(", ");
            }
            BigDecimal precioReferencia = c.getExpectativaPrecio() != null
                    ? c.getExpectativaPrecio()
                    : c.getPrecioMinimoAceptado();
            p.append("precio: ").append(precioReferencia).append(" COP, estado: ")
                    .append(c.getEstadoCaptacion()).append("\n");
        }

        return p.toString();
    }

    private void registrarHistorial(Inmueble inmueble, PrecioSugeridoIA resultado) {
        HistorialCaptacion historial = HistorialCaptacion.builder()
                .inmueble(inmueble)
                .evento("Precio sugerido por IA: " + resultado.precioSugerido() + " COP (rango "
                        + resultado.rangoMinimo() + " - " + resultado.rangoMaximo() + ")")
                .build();

        historialCaptacionRepository.save(historial);
    }
}
