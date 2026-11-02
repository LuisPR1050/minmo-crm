package com.felipeparodi.crmbackend.captacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Message;
import com.anthropic.models.messages.MessageCreateParams;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.documentacion.util.LimpiadorMarkdownIA;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Genera, para la landing publica, una frase corta y emocional que despierte el interes del
 * visitante y le genere la necesidad de preguntar por mas (no reemplaza la descripcion publica
 * larga, es un gancho adicional que se muestra destacado arriba en la landing).
 */
@Service
@Transactional
public class GanchoEmocionalIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final InmuebleRepository inmuebleRepository;
    private final InmuebleService inmuebleService;

    public GanchoEmocionalIAService(AnthropicClient anthropicClient, InmuebleRepository inmuebleRepository,
                                     InmuebleService inmuebleService) {
        this.anthropicClient = anthropicClient;
        this.inmuebleRepository = inmuebleRepository;
        this.inmuebleService = inmuebleService;
    }

    public InmuebleResponse generar(UUID inmuebleId) {
        Inmueble inmueble = inmuebleRepository.findById(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + inmuebleId));

        String prompt = construirPrompt(inmueble);

        MessageCreateParams params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(300L)
                .addUserMessage(prompt)
                .build();

        Message response = anthropicClient.messages().create(params);

        String texto = response.content().stream()
                .flatMap(block -> block.text().stream())
                .map(block -> block.text())
                .collect(Collectors.joining(" "))
                .trim();

        if (texto.isBlank()) {
            throw new IllegalStateException("La IA no genero un gancho emocional para el inmueble " + inmuebleId);
        }

        texto = LimpiadorMarkdownIA.limpiar(texto).replace("\"", "").trim();

        inmueble.setGanchoEmocional(texto);
        inmuebleRepository.save(inmueble);

        return inmuebleService.buscarPorId(inmuebleId);
    }

    private String construirPrompt(Inmueble inmueble) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un copywriter inmobiliario experto en ventas emocionales. Escribe UNA sola frase corta ");
        p.append("(maximo 25 palabras) para mostrarse destacada arriba del titulo en la landing publica de un ");
        p.append("inmueble. El objetivo es despertar deseo y generar en el lector la necesidad de preguntar por ");
        p.append("mas informacion; no es una descripcion tecnica, es un gancho emocional de venta. No uses ");
        p.append("comillas, asteriscos, emojis ni formato Markdown; devuelve UNICAMENTE la frase, sin explicaciones.\n\n");

        p.append("DATOS DE LA PROPIEDAD\n");
        p.append("Tipo: ").append(inmueble.getTipoPropiedad()).append("\n");
        if (inmueble.getZona() != null) {
            p.append("Zona: ").append(inmueble.getZona()).append("\n");
        }
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getNumeroHabitaciones() != null) {
            p.append("Habitaciones: ").append(inmueble.getNumeroHabitaciones()).append("\n");
        }
        if (inmueble.getNumeroBanos() != null) {
            p.append("Banos: ").append(inmueble.getNumeroBanos()).append("\n");
        }
        if (inmueble.getVista() != null) {
            p.append("Vista: ").append(inmueble.getVista()).append("\n");
        }
        if (inmueble.getEntorno() != null) {
            p.append("Entorno: ").append(inmueble.getEntorno()).append("\n");
        }
        if (inmueble.getDescripcionPublica() != null && !inmueble.getDescripcionPublica().isBlank()) {
            p.append("Descripcion existente: ").append(inmueble.getDescripcionPublica()).append("\n");
        }

        return p.toString();
    }
}
