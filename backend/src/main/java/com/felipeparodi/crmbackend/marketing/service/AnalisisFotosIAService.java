package com.felipeparodi.crmbackend.marketing.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.marketing.dto.AnalisisFotosIA;
import com.felipeparodi.crmbackend.marketing.dto.AnalisisFotosResponse;
import com.felipeparodi.crmbackend.marketing.entity.FotoPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.FotoPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
@Transactional
public class AnalisisFotosIAService {

    private static final String MODELO = "claude-opus-5";
    private static final int MAXIMO_FOTOS_A_ANALIZAR = 10;

    private final AnthropicClient anthropicClient;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final FotoPublicacionRepository fotoPublicacionRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public AnalisisFotosIAService(AnthropicClient anthropicClient,
                                   PublicacionInmuebleRepository publicacionInmuebleRepository,
                                   FotoPublicacionRepository fotoPublicacionRepository,
                                   AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.anthropicClient = anthropicClient;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.fotoPublicacionRepository = fotoPublicacionRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public AnalisisFotosResponse analizar(UUID publicacionId) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionId));

        Inmueble inmueble = publicacion.getInmueble();

        List<FotoPublicacion> fotos = fotoPublicacionRepository.findByPublicacionInmuebleId(publicacionId)
                .stream()
                .limit(MAXIMO_FOTOS_A_ANALIZAR)
                .toList();

        if (fotos.isEmpty()) {
            throw new IllegalArgumentException(
                    "Esta publicacion no tiene fotos cargadas para analizar. Sube fotos en la pestana \"Fotos\".");
        }

        List<ContentBlockParam> bloques = new ArrayList<>();
        bloques.add(ContentBlockParam.ofText(TextBlockParam.builder().text(construirPrompt(inmueble, fotos.size())).build()));

        for (FotoPublicacion foto : fotos) {
            byte[] bytes = almacenamientoArchivosService.leerBytes(foto.getUrl());
            String base64 = Base64.getEncoder().encodeToString(bytes);

            bloques.add(ContentBlockParam.ofImage(ImageBlockParam.builder()
                    .source(Base64ImageSource.builder()
                            .data(base64)
                            .mediaType(mediaTypeDeUrl(foto.getUrl()))
                            .build())
                    .build()));
        }

        StructuredMessageCreateParams<AnalisisFotosIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1536L)
                .outputConfig(AnalisisFotosIA.class)
                .addUserMessageOfBlockParams(bloques)
                .build();

        AnalisisFotosIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero un analisis de fotos para la publicacion " + publicacionId));

        return AnalisisFotosResponse.builder()
                .publicacionId(publicacionId)
                .puntuacionGeneral(resultado.puntuacionGeneral())
                .fortalezas(resultado.fortalezas())
                .problemas(resultado.problemas())
                .recomendaciones(resultado.recomendaciones())
                .fotosFaltantes(resultado.fotosFaltantes())
                .fotosAnalizadas(fotos.size())
                .build();
    }

    private String construirPrompt(Inmueble inmueble, int totalFotos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un fotografo inmobiliario experto en Colombia. Evalua estas ").append(totalFotos);
        p.append(" fotos de una propiedad (").append(inmueble.getTipoPropiedad());
        if (inmueble.getZona() != null) {
            p.append(", zona ").append(inmueble.getZona());
        }
        p.append(") que se van a usar para publicarla en portales inmobiliarios y redes sociales. ");
        p.append("Evalua calidad, iluminacion, orden/composicion, angulos, resolucion, y si el set de fotos ");
        p.append("cubre los espacios clave (fachada, sala, cocina, habitaciones, banos, zonas comunes segun aplique). ");
        p.append("Basate solo en lo que ves en las imagenes.");

        return p.toString();
    }

    private Base64ImageSource.MediaType mediaTypeDeUrl(String url) {
        String urlEnMinuscula = url.toLowerCase(Locale.ROOT);
        if (urlEnMinuscula.endsWith(".png")) {
            return Base64ImageSource.MediaType.IMAGE_PNG;
        }
        if (urlEnMinuscula.endsWith(".webp")) {
            return Base64ImageSource.MediaType.IMAGE_WEBP;
        }
        return Base64ImageSource.MediaType.IMAGE_JPEG;
    }
}
