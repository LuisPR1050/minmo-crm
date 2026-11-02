package com.felipeparodi.crmbackend.marketing.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.InmuebleCaracteristica;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.marketing.dto.DescripcionesGeneradasIA;
import com.felipeparodi.crmbackend.marketing.dto.PublicacionInmuebleResponse;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class DescripcionPublicacionIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    private final HistorialPublicacionRepository historialPublicacionRepository;

    public DescripcionPublicacionIAService(AnthropicClient anthropicClient,
                                            PublicacionInmuebleRepository publicacionInmuebleRepository,
                                            InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository,
                                            HistorialPublicacionRepository historialPublicacionRepository) {
        this.anthropicClient = anthropicClient;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.inmuebleCaracteristicaRepository = inmuebleCaracteristicaRepository;
        this.historialPublicacionRepository = historialPublicacionRepository;
    }

    public PublicacionInmuebleResponse generarDescripciones(UUID publicacionInmuebleId) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionInmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionInmuebleId));

        String prompt = construirPrompt(publicacion);

        StructuredMessageCreateParams<DescripcionesGeneradasIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(2048L)
                .outputConfig(DescripcionesGeneradasIA.class)
                .addUserMessage(prompt)
                .build();

        DescripcionesGeneradasIA generadas = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero una respuesta valida para la publicacion " + publicacionInmuebleId));

        publicacion.setDescripcionCorta(generadas.descripcionCorta());
        publicacion.setDescripcionLarga(generadas.descripcionLarga());
        publicacion.setDescripcionOptimizada(generadas.descripcionOptimizada());

        PublicacionInmueble guardada = publicacionInmuebleRepository.save(publicacion);

        registrarHistorial(guardada, "Descripciones generadas por IA (corta, larga, optimizada)");

        return mapearAResponse(guardada);
    }

    private String construirPrompt(PublicacionInmueble publicacion) {
        Inmueble inmueble = publicacion.getInmueble();

        List<String> caracteristicas = inmuebleCaracteristicaRepository.findByInmuebleId(inmueble.getId())
                .stream()
                .map(InmuebleCaracteristica::getCaracteristica)
                .map(c -> c.getNombre())
                .collect(Collectors.toList());

        StringBuilder datos = new StringBuilder();
        datos.append("Redacta descripciones de venta para la siguiente propiedad inmobiliaria en Colombia.\n\n");
        datos.append("Codigo: ").append(inmueble.getCodigo()).append("\n");
        datos.append("Tipo de propiedad: ").append(inmueble.getTipoPropiedad()).append("\n");
        datos.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getZona() != null) {
            datos.append("Zona: ").append(inmueble.getZona()).append("\n");
        }
        if (inmueble.getAreaTotal() != null) {
            datos.append("Area total: ").append(inmueble.getAreaTotal()).append(" m2\n");
        }
        if (inmueble.getAreaConstruida() != null) {
            datos.append("Area construida: ").append(inmueble.getAreaConstruida()).append(" m2\n");
        }
        if (inmueble.getNumeroHabitaciones() != null) {
            datos.append("Habitaciones: ").append(inmueble.getNumeroHabitaciones()).append("\n");
        }
        if (inmueble.getNumeroBanos() != null) {
            datos.append("Banos: ").append(inmueble.getNumeroBanos()).append("\n");
        }
        if (inmueble.getEstratoSocioeconomico() != null) {
            datos.append("Estrato: ").append(inmueble.getEstratoSocioeconomico()).append("\n");
        }
        if (inmueble.getVista() != null) {
            datos.append("Vista: ").append(inmueble.getVista()).append("\n");
        }
        if (inmueble.getPuntosClavesCercanos() != null) {
            datos.append("Puntos cercanos: ").append(inmueble.getPuntosClavesCercanos()).append("\n");
        }
        if (!caracteristicas.isEmpty()) {
            datos.append("Caracteristicas destacadas: ").append(String.join(", ", caracteristicas)).append("\n");
        }
        if (inmueble.getExpectativaPrecio() != null) {
            datos.append("Precio: ").append(inmueble.getExpectativaPrecio()).append(" COP\n");
        }
        datos.append("Operacion: ").append(publicacion.getTipoOperacion()).append("\n");

        datos.append("\nNo inventes datos que no esten en esta lista. Si falta un dato, simplemente no lo menciones.");

        return datos.toString();
    }

    private void registrarHistorial(PublicacionInmueble publicacion, String evento) {
        HistorialPublicacion historial = HistorialPublicacion.builder()
                .publicacionInmueble(publicacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialPublicacionRepository.save(historial);
    }

    private PublicacionInmuebleResponse mapearAResponse(PublicacionInmueble publicacion) {
        return PublicacionInmuebleResponse.builder()
                .id(publicacion.getId())
                .inmuebleId(publicacion.getInmueble().getId())
                .inmuebleCodigo(publicacion.getInmueble().getCodigo())
                .tipoOperacion(publicacion.getTipoOperacion() != null ? publicacion.getTipoOperacion().name() : null)
                .mostrarPrecio(publicacion.isMostrarPrecio())
                .mostrarContacto(publicacion.isMostrarContacto())
                .mostrarCorreo(publicacion.isMostrarCorreo())
                .comisionCompartida(publicacion.isComisionCompartida())
                .destacada(publicacion.isDestacada())
                .descripcionCorta(publicacion.getDescripcionCorta())
                .descripcionLarga(publicacion.getDescripcionLarga())
                .descripcionOptimizada(publicacion.getDescripcionOptimizada())
                .estado(publicacion.getEstado().name())
                .fechaPublicacion(publicacion.getFechaPublicacion())
                .createdAt(publicacion.getCreatedAt())
                .build();
    }
}
