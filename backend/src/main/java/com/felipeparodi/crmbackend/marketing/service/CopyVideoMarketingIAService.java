package com.felipeparodi.crmbackend.marketing.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.InmuebleCaracteristica;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.marketing.dto.CopiaVideoCanalResponse;
import com.felipeparodi.crmbackend.marketing.dto.CopyPorCanalIA;
import com.felipeparodi.crmbackend.marketing.dto.CopyVideoMarketingIA;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingResponse;
import com.felipeparodi.crmbackend.marketing.entity.CanalPublicacionVideo;
import com.felipeparodi.crmbackend.marketing.entity.CopiaVideoCanal;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoCanal;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoTipoPublico;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.CopiaVideoCanalRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoCanalRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoTipoPublicoRepository;
import com.felipeparodi.crmbackend.documentacion.util.LimpiadorMarkdownIA;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class CopyVideoMarketingIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final SolicitudVideoCanalRepository solicitudVideoCanalRepository;
    private final SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository;
    private final InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    private final CopiaVideoCanalRepository copiaVideoCanalRepository;

    public CopyVideoMarketingIAService(AnthropicClient anthropicClient,
                                        SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                        SolicitudVideoCanalRepository solicitudVideoCanalRepository,
                                        SolicitudVideoTipoPublicoRepository solicitudVideoTipoPublicoRepository,
                                        InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository,
                                        CopiaVideoCanalRepository copiaVideoCanalRepository) {
        this.anthropicClient = anthropicClient;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.solicitudVideoCanalRepository = solicitudVideoCanalRepository;
        this.solicitudVideoTipoPublicoRepository = solicitudVideoTipoPublicoRepository;
        this.inmuebleCaracteristicaRepository = inmuebleCaracteristicaRepository;
        this.copiaVideoCanalRepository = copiaVideoCanalRepository;
    }

    public SolicitudVideoMarketingResponse generarCopy(UUID solicitudVideoId, String instruccionesAdicionales) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudVideoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Solicitud de video no encontrada con id: " + solicitudVideoId));

        List<SolicitudVideoCanal> canalesSeleccionados = solicitudVideoCanalRepository
                .findBySolicitudVideoId(solicitudVideoId);

        String prompt = construirPrompt(solicitud, canalesSeleccionados, instruccionesAdicionales);

        StructuredMessageCreateParams<CopyVideoMarketingIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(3000L)
                .outputConfig(CopyVideoMarketingIA.class)
                .addUserMessage(prompt)
                .build();

        CopyVideoMarketingIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero copy valido para la solicitud de video " + solicitudVideoId));

        solicitud.setTituloGenerado(LimpiadorMarkdownIA.limpiar(resultado.titulo()));
        solicitud.setSubtituloGenerado(LimpiadorMarkdownIA.limpiar(resultado.subtitulo()));
        solicitud.setDescripcionGenerada(LimpiadorMarkdownIA.limpiar(resultado.descripcion()));
        solicitud.setLlamadoAccionTexto(LimpiadorMarkdownIA.limpiar(resultado.llamadoAccionTexto()));
        solicitud.setHashtags(resultado.hashtags().stream()
                .map(h -> "#" + h.replaceFirst("^#", ""))
                .collect(Collectors.joining(" ")));
        solicitud.setFechaGeneracionCopy(LocalDateTime.now());

        SolicitudVideoMarketing guardada = solicitudVideoMarketingRepository.save(solicitud);

        guardarCopiasPorCanal(guardada, canalesSeleccionados, resultado.copiasPorCanal());

        return mapearAResponse(guardada);
    }

    public List<CopiaVideoCanalResponse> listarCopiasPorCanal(UUID solicitudVideoId) {
        return copiaVideoCanalRepository.findBySolicitudVideoId(solicitudVideoId)
                .stream()
                .map(c -> CopiaVideoCanalResponse.builder()
                        .id(c.getId())
                        .canalNombre(c.getCanalPublicacionVideo().getNombre())
                        .texto(c.getTexto())
                        .build())
                .collect(Collectors.toList());
    }

    private void guardarCopiasPorCanal(SolicitudVideoMarketing solicitud,
                                        List<SolicitudVideoCanal> canalesSeleccionados,
                                        List<CopyPorCanalIA> copiasGeneradas) {
        copiaVideoCanalRepository.deleteBySolicitudVideoId(solicitud.getId());

        Map<String, CanalPublicacionVideo> canalesPorNombre = canalesSeleccionados.stream()
                .map(SolicitudVideoCanal::getCanalPublicacionVideo)
                .collect(Collectors.toMap(CanalPublicacionVideo::getNombre, c -> c, (a, b) -> a));

        for (CopyPorCanalIA copia : copiasGeneradas) {
            CanalPublicacionVideo canal = canalesPorNombre.get(copia.canal());
            if (canal == null) {
                continue;
            }

            CopiaVideoCanal entidad = CopiaVideoCanal.builder()
                    .solicitudVideo(solicitud)
                    .canalPublicacionVideo(canal)
                    .texto(copia.texto())
                    .build();

            copiaVideoCanalRepository.save(entidad);
        }
    }

    private String construirPrompt(SolicitudVideoMarketing solicitud, List<SolicitudVideoCanal> canalesSeleccionados,
                                    String instruccionesAdicionales) {
        Inmueble inmueble = solicitud.getInmueble();

        List<String> caracteristicas = inmuebleCaracteristicaRepository.findByInmuebleId(inmueble.getId())
                .stream()
                .map(InmuebleCaracteristica::getCaracteristica)
                .map(c -> c.getNombre())
                .collect(Collectors.toList());

        List<String> tiposPublico = solicitudVideoTipoPublicoRepository.findBySolicitudVideoId(solicitud.getId())
                .stream()
                .map(SolicitudVideoTipoPublico::getTipoPublicoVideo)
                .map(t -> t.getNombre())
                .collect(Collectors.toList());

        List<String> canales = canalesSeleccionados.stream()
                .map(SolicitudVideoCanal::getCanalPublicacionVideo)
                .map(CanalPublicacionVideo::getNombre)
                .collect(Collectors.toList());

        StringBuilder p = new StringBuilder();
        p.append("Eres un redactor de marketing inmobiliario para redes sociales en Colombia. Genera el copy ");
        p.append("(titulo, subtitulo, descripcion, llamado a la accion y hashtags) para un video promocional ");
        p.append("de la siguiente propiedad, y adapta una version del texto para cada canal de publicacion ");
        p.append("seleccionado. No inventes datos de la propiedad que no esten en esta lista. Escribe en prosa ");
        p.append("clara y profesional, sin formato Markdown: nada de asteriscos (*), dobles asteriscos (**) ni ");
        p.append("numerales (#); el titulo y el llamado a la accion se van a mostrar como texto superpuesto en ");
        p.append("el video, asi que deben ser cortos y leerse bien en pantalla.\n\n");

        p.append("PROPIEDAD\n");
        p.append("Codigo: ").append(inmueble.getCodigo()).append("\n");
        p.append("Tipo: ").append(inmueble.getTipoPropiedad()).append("\n");
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getZona() != null) {
            p.append("Zona: ").append(inmueble.getZona()).append("\n");
        }
        if (inmueble.getNumeroHabitaciones() != null) {
            p.append("Habitaciones: ").append(inmueble.getNumeroHabitaciones()).append("\n");
        }
        if (inmueble.getNumeroBanos() != null) {
            p.append("Banos: ").append(inmueble.getNumeroBanos()).append("\n");
        }
        if (inmueble.getAreaTotal() != null) {
            p.append("Area: ").append(inmueble.getAreaTotal()).append(" m2\n");
        }
        if (inmueble.getExpectativaPrecio() != null) {
            p.append("Precio: ").append(inmueble.getExpectativaPrecio()).append(" COP\n");
        }
        if (!caracteristicas.isEmpty()) {
            p.append("Caracteristicas destacadas: ").append(String.join(", ", caracteristicas)).append("\n");
        }

        p.append("\nPARAMETROS DEL VIDEO\n");
        p.append("Objetivo de la publicacion: ").append(solicitud.getObjetivoPublicacion()).append("\n");
        if (solicitud.getEstiloContenido() != null) {
            p.append("Estilo del contenido: ").append(solicitud.getEstiloContenido()).append("\n");
        }
        String hintDuracion = construirHintDuracion(solicitud);
        if (hintDuracion != null) {
            p.append(hintDuracion);
        }
        if (solicitud.getLlamadoAccion() != null) {
            p.append("Tipo de llamado a la accion: ").append(solicitud.getLlamadoAccion()).append("\n");
        }
        if (!tiposPublico.isEmpty()) {
            p.append("Publico objetivo: ").append(String.join(", ", tiposPublico)).append("\n");
        }

        p.append("\nCANALES SELECCIONADOS (genera una variante de copy para cada uno, usando el nombre exacto)\n");
        if (canales.isEmpty()) {
            p.append("(No se selecciono ningun canal; deja la lista de copiasPorCanal vacia)\n");
        } else {
            canales.forEach(c -> p.append("- ").append(c).append("\n"));
        }

        if (instruccionesAdicionales != null && !instruccionesAdicionales.isBlank()) {
            p.append("\nINSTRUCCIONES ADICIONALES DEL USUARIO (ten en cuenta esto al redactar)\n");
            p.append(instruccionesAdicionales.trim()).append("\n");
        }

        return p.toString();
    }

    /**
     * Si la solicitud tiene un rango de duracion objetivo para el video (en minutos), le indica
     * a la IA cuantas palabras aproximadamente debe tener el texto para que, leido en voz alta
     * a un ritmo natural (~155 palabras por minuto en espanol), el video quede dentro de ese rango.
     */
    private String construirHintDuracion(SolicitudVideoMarketing solicitud) {
        Double min = solicitud.getDuracionMinimaMinutos();
        Double max = solicitud.getDuracionMaximaMinutos();
        if (min == null && max == null) {
            return null;
        }

        double palabrasPorMinuto = 155;
        double minMinutos = min != null ? min : max;
        double maxMinutos = max != null ? max : min;
        long palabrasMin = Math.round(minMinutos * palabrasPorMinuto);
        long palabrasMax = Math.round(maxMinutos * palabrasPorMinuto);

        return "Duracion objetivo del video: entre " + minMinutos + " y " + maxMinutos + " minutos. "
                + "El titulo, la descripcion y el llamado a la accion, leidos todos en voz alta en conjunto, "
                + "deben sumar aproximadamente entre " + palabrasMin + " y " + palabrasMax + " palabras en total.\n";
    }

    private SolicitudVideoMarketingResponse mapearAResponse(SolicitudVideoMarketing solicitud) {
        return SolicitudVideoMarketingResponse.builder()
                .id(solicitud.getId())
                .inmuebleId(solicitud.getInmueble().getId())
                .inmuebleCodigo(solicitud.getInmueble().getCodigo())
                .objetivoPublicacion(solicitud.getObjetivoPublicacion() != null
                        ? solicitud.getObjetivoPublicacion().name() : null)
                .estiloContenido(solicitud.getEstiloContenido() != null ? solicitud.getEstiloContenido().name() : null)
                .duracionMinimaMinutos(solicitud.getDuracionMinimaMinutos())
                .duracionMaximaMinutos(solicitud.getDuracionMaximaMinutos())
                .llamadoAccion(solicitud.getLlamadoAccion() != null ? solicitud.getLlamadoAccion().name() : null)
                .logoUrl(solicitud.getLogoUrl())
                .musicaFondoUrl(solicitud.getMusicaFondoUrl())
                .estado(solicitud.getEstado().name())
                .videoGeneradoUrl(solicitud.getVideoGeneradoUrl())
                .tituloGenerado(solicitud.getTituloGenerado())
                .subtituloGenerado(solicitud.getSubtituloGenerado())
                .descripcionGenerada(solicitud.getDescripcionGenerada())
                .llamadoAccionTexto(solicitud.getLlamadoAccionTexto())
                .hashtags(solicitud.getHashtags())
                .fechaGeneracionCopy(solicitud.getFechaGeneracionCopy())
                .createdAt(solicitud.getCreatedAt())
                .build();
    }
}
