package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.config.AlmacenamientoVideoService;
import com.felipeparodi.crmbackend.documentacion.util.ArchivoEnMemoria;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingResponse;
import com.felipeparodi.crmbackend.marketing.entity.EstadoSolicitudVideo;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.TipoOrigenArchivo;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.FotoSolicitudVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoFuenteSolicitudRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Arma un video-slideshow real (fotos con efecto Ken Burns + clips de video + transiciones de
 * fundido + texto animado superpuesto + voz en off opcional) para una solicitud de video de
 * marketing, usando ffmpeg y Piper (TTS neuronal local) como binarios externos. No depende de
 * ninguna IA de pago: el "video" se arma localmente a partir del material ya captado (fotos y
 * videos subidos en captacion) y del copy (titulo/descripcion/CTA) que ya genera
 * CopyVideoMarketingIAService.
 */
@Service
public class VideoSlideshowService {

    private static final Logger log = LoggerFactory.getLogger(VideoSlideshowService.class);

    private static final String SUBCARPETA_VIDEO = "videos-marketing";
    private static final double DURACION_POR_FOTO_SEGUNDOS = 3;
    private static final double DURACION_MAXIMA_CLIP_SEGUNDOS = 5;
    private static final double DURACION_MINIMA_SEGMENTO_CON_VOZ = 2;
    private static final double DURACION_MAXIMA_SEGMENTO_CON_VOZ = 8;
    private static final double VOLUMEN_MUSICA_FONDO = 0.5;
    private static final double VOLUMEN_MUSICA_CON_VOZ = 0.15;
    private static final double TRANSICION_SEGUNDOS = 0.6;
    private static final int FPS = 25;
    private static final int ANCHO = 1080;
    private static final int ALTO = 1080;

    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final FotoSolicitudVideoRepository fotoSolicitudVideoRepository;
    private final VideoFuenteSolicitudRepository videoFuenteSolicitudRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;
    private final AlmacenamientoVideoService almacenamientoVideoService;
    private final SolicitudVideoMarketingService solicitudVideoMarketingService;

    private final String ffmpegPath;
    private final String fontPath;
    private final String musicaFondoPath;
    private final String piperPath;
    private final String piperVozModeloPath;

    public VideoSlideshowService(SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                                  FotoSolicitudVideoRepository fotoSolicitudVideoRepository,
                                  VideoFuenteSolicitudRepository videoFuenteSolicitudRepository,
                                  AlmacenamientoArchivosService almacenamientoArchivosService,
                                  AlmacenamientoVideoService almacenamientoVideoService,
                                  SolicitudVideoMarketingService solicitudVideoMarketingService,
                                  @Value("${ffmpeg.path:ffmpeg}") String ffmpegPath,
                                  @Value("${ffmpeg.font-path:}") String fontPath,
                                  @Value("${ffmpeg.musica-fondo-path:}") String musicaFondoPath,
                                  @Value("${piper.path:}") String piperPath,
                                  @Value("${piper.voz-modelo-path:}") String piperVozModeloPath) {
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.fotoSolicitudVideoRepository = fotoSolicitudVideoRepository;
        this.videoFuenteSolicitudRepository = videoFuenteSolicitudRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.almacenamientoVideoService = almacenamientoVideoService;
        this.solicitudVideoMarketingService = solicitudVideoMarketingService;
        this.ffmpegPath = ffmpegPath;
        this.fontPath = fontPath;
        this.musicaFondoPath = musicaFondoPath.isBlank() ? extraerMusicaAmbientalPorDefecto() : musicaFondoPath;
        this.piperPath = piperPath;
        this.piperVozModeloPath = piperVozModeloPath;

        if (this.fontPath.isBlank()) {
            log.warn("ffmpeg.font-path no esta configurado: el video-slideshow se generara sin texto superpuesto.");
        }
        if (this.musicaFondoPath.isBlank()) {
            log.warn("No hay musica de fondo configurada ni disponible por defecto: el video-slideshow se generara sin musica.");
        } else {
            log.info("Musica de fondo del video-slideshow: {}", this.musicaFondoPath);
        }
        if (this.piperPath.isBlank() || this.piperVozModeloPath.isBlank()) {
            log.warn("piper.path / piper.voz-modelo-path no estan configurados: la voz en off no estara disponible.");
        }
    }

    /**
     * Si no se configuro una pista real (ffmpeg.musica-fondo-path), usa un tono ambiental
     * generado por software (sin derechos de autor, no es una grabacion de nadie) que viene
     * empaquetado en el jar. Se extrae a un archivo temporal porque ffmpeg necesita una ruta
     * real en disco, no un recurso dentro del jar.
     */
    private String extraerMusicaAmbientalPorDefecto() {
        ClassPathResource recurso = new ClassPathResource("audio/musica-ambiental.mp3");
        if (!recurso.exists()) {
            return "";
        }
        try {
            Path temporal = Files.createTempFile("musica-ambiental-", ".mp3");
            temporal.toFile().deleteOnExit();
            try (var entrada = recurso.getInputStream()) {
                Files.copy(entrada, temporal, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
            return temporal.toString();
        } catch (IOException e) {
            log.warn("No se pudo extraer la musica ambiental por defecto: {}", e.getMessage());
            return "";
        }
    }

    private record MaterialFuente(boolean esVideo, String url, LocalDateTime createdAt) {
    }

    public SolicitudVideoMarketingResponse generarVideo(UUID solicitudId, boolean incluirVozOff) {
        SolicitudVideoMarketing solicitud = solicitudVideoMarketingRepository.findById(solicitudId)
                .orElseThrow(() -> new RecursoNoEncontradoException("Solicitud de video no encontrada con id: " + solicitudId));

        List<MaterialFuente> material = recolectarMaterial(solicitudId);

        if (material.isEmpty()) {
            throw new IllegalArgumentException(
                    "Selecciona al menos una foto o un video subido antes de generar el video");
        }

        solicitud.setEstado(EstadoSolicitudVideo.EN_PROCESO);
        solicitudVideoMarketingRepository.save(solicitud);

        Path directorioTemporal;
        try {
            directorioTemporal = Files.createTempDirectory("slideshow-" + solicitudId);
        } catch (IOException e) {
            solicitud.setEstado(EstadoSolicitudVideo.FALLIDO);
            solicitudVideoMarketingRepository.save(solicitud);
            throw new UncheckedIOException("No se pudo crear el directorio temporal para el video", e);
        }

        try {
            Double objetivoSegundos = calcularObjetivoSegundos(solicitud);

            Path narracion = incluirVozOff ? generarNarracion(directorioTemporal, solicitud, 1.0) : null;
            if (narracion != null && objetivoSegundos != null) {
                narracion = ajustarNarracionADuracionObjetivo(directorioTemporal, solicitud, narracion, objetivoSegundos);
            }

            double duracionFoto = DURACION_POR_FOTO_SEGUNDOS;
            double duracionClip = DURACION_MAXIMA_CLIP_SEGUNDOS;
            if (objetivoSegundos != null) {
                // El rango en minutos que elige el usuario manda sobre el ritmo visual: la
                // narracion se ajusta (dentro de limites razonables) para acompanarlo, pero no
                // al reves, para que el video realmente dure lo que el usuario pidio.
                double duracionPorSegmento = Math.max(1.5, Math.min(20, objetivoSegundos / material.size()));
                duracionFoto = duracionPorSegmento;
                duracionClip = duracionPorSegmento;
            } else if (narracion != null) {
                double duracionNarracion = duracionDeArchivo(narracion);
                if (duracionNarracion > 0) {
                    double duracionPorSegmento = duracionNarracion / material.size();
                    duracionPorSegmento = Math.max(DURACION_MINIMA_SEGMENTO_CON_VOZ,
                            Math.min(DURACION_MAXIMA_SEGMENTO_CON_VOZ, duracionPorSegmento));
                    duracionFoto = duracionPorSegmento;
                    duracionClip = duracionPorSegmento;
                }
            }

            List<Path> segmentos = new ArrayList<>();
            for (int i = 0; i < material.size(); i++) {
                MaterialFuente item = material.get(i);
                boolean esPrimero = segmentos.isEmpty();
                boolean esUltimo = i == material.size() - 1;
                String texto = null;
                if (esPrimero && solicitud.getTituloGenerado() != null && !solicitud.getTituloGenerado().isBlank()) {
                    texto = solicitud.getTituloGenerado();
                } else if (esUltimo && solicitud.getLlamadoAccionTexto() != null && !solicitud.getLlamadoAccionTexto().isBlank()) {
                    texto = solicitud.getLlamadoAccionTexto();
                }

                try {
                    Path segmento = item.esVideo()
                            ? generarSegmentoVideo(directorioTemporal, item.url(), i, texto, duracionClip)
                            : generarSegmentoFoto(directorioTemporal, item.url(), i, texto, duracionFoto);
                    segmentos.add(segmento);
                } catch (RuntimeException e) {
                    log.warn("Se omite un elemento del material (url={}) porque no se pudo procesar: {}",
                            item.url(), e.getMessage());
                }
            }

            if (segmentos.isEmpty()) {
                throw new IllegalStateException(
                        "No se pudo generar el video: ninguna de las fotos o videos seleccionados se pudo leer. "
                                + "Puede que ese material ya no exista; quitalo de la solicitud e intenta de nuevo.");
            }

            Path videoFinal = unirSegmentosConTransicion(directorioTemporal, segmentos);

            String musicaEfectiva = resolverMusicaFondo(directorioTemporal, solicitud);
            boolean hayMusica = musicaEfectiva != null;
            if (narracion != null && hayMusica) {
                videoFinal = agregarNarracionYMusica(directorioTemporal, videoFinal, narracion, musicaEfectiva);
            } else if (narracion != null) {
                videoFinal = agregarSoloNarracion(directorioTemporal, videoFinal, narracion);
            } else if (hayMusica) {
                videoFinal = agregarMusicaDeFondo(directorioTemporal, videoFinal, musicaEfectiva);
            }

            byte[] videoBytes = Files.readAllBytes(videoFinal);
            ArchivoEnMemoria archivo = new ArchivoEnMemoria("video-marketing.mp4", "video/mp4", videoBytes);

            String urlPublica = almacenamientoVideoService.guardarVideo(SUBCARPETA_VIDEO, archivo);

            solicitud.setVideoGeneradoUrl(urlPublica);
            solicitud.setEstado(EstadoSolicitudVideo.GENERADO);
            solicitudVideoMarketingRepository.save(solicitud);

            return solicitudVideoMarketingService.buscarPorId(solicitudId);
        } catch (IOException e) {
            solicitud.setEstado(EstadoSolicitudVideo.FALLIDO);
            solicitudVideoMarketingRepository.save(solicitud);
            throw new UncheckedIOException("No se pudo generar el video del slideshow", e);
        } catch (RuntimeException e) {
            solicitud.setEstado(EstadoSolicitudVideo.FALLIDO);
            solicitudVideoMarketingRepository.save(solicitud);
            throw e;
        } finally {
            borrarDirectorio(directorioTemporal);
        }
    }

    private List<MaterialFuente> recolectarMaterial(UUID solicitudId) {
        List<MaterialFuente> material = new ArrayList<>();

        fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)
                .forEach(f -> material.add(new MaterialFuente(false, f.getUrl(), f.getCreatedAt())));

        videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId).stream()
                .filter(v -> v.getTipoOrigen() == TipoOrigenArchivo.SUBIDO)
                .forEach(v -> material.add(new MaterialFuente(true, v.getUrl(), v.getCreatedAt())));

        return material.stream()
                .sorted(Comparator.comparing(MaterialFuente::createdAt))
                .collect(Collectors.toList());
    }

    /**
     * Calcula, en segundos, la duracion objetivo del video a partir del rango en minutos
     * configurado en la solicitud (promedio si estan los dos extremos, o el unico valor
     * presente). Si no se configuro ningun rango, devuelve null y el video usa los tiempos
     * automaticos de siempre.
     */
    private Double calcularObjetivoSegundos(SolicitudVideoMarketing solicitud) {
        Double min = solicitud.getDuracionMinimaMinutos();
        Double max = solicitud.getDuracionMaximaMinutos();
        Double objetivoMinutos;
        if (min != null && max != null) {
            objetivoMinutos = (min + max) / 2.0;
        } else {
            objetivoMinutos = min != null ? min : max;
        }
        return objetivoMinutos != null ? objetivoMinutos * 60 : null;
    }

    /**
     * Si la narracion generada a ritmo normal queda muy lejos de la duracion objetivo, la
     * regenera ajustando la velocidad de lectura de Piper (length_scale: mas alto = mas lento).
     * El ajuste se limita a un rango razonable (0.75-1.35) para no sonar forzado; si con eso no
     * alcanza a cuadrar exacto, se deja la narracion lo mas cerca posible en vez de deformarla.
     */
    private Path ajustarNarracionADuracionObjetivo(Path directorioTemporal, SolicitudVideoMarketing solicitud,
                                                      Path narracionOriginal, double objetivoSegundos) {
        double duracionNatural = duracionDeArchivo(narracionOriginal);
        if (duracionNatural <= 0) {
            return narracionOriginal;
        }

        double factor = objetivoSegundos / duracionNatural;
        double lengthScaleAjustado = Math.max(0.75, Math.min(1.35, factor));
        if (Math.abs(lengthScaleAjustado - 1.0) < 0.03) {
            return narracionOriginal;
        }

        Path narracionAjustada = generarNarracion(directorioTemporal, solicitud, lengthScaleAjustado);
        return narracionAjustada != null ? narracionAjustada : narracionOriginal;
    }

    /**
     * Sintetiza con Piper (TTS neuronal local, voz es_MX-claude-high) una narracion a partir
     * del titulo, la descripcion y el llamado a la accion generados por IA. Si no hay texto, Piper
     * no esta configurado o falla, devuelve null (el video se genera igual, solo que sin voz en off).
     * lengthScale controla la velocidad de lectura (1.0 = normal, mayor = mas lento/pausado).
     */
    private Path generarNarracion(Path directorioTemporal, SolicitudVideoMarketing solicitud, double lengthScale) {
        StringBuilder guion = new StringBuilder();
        agregarSiNoVacio(guion, solicitud.getTituloGenerado());
        agregarSiNoVacio(guion, solicitud.getDescripcionGenerada());
        agregarSiNoVacio(guion, solicitud.getLlamadoAccionTexto());

        if (guion.isEmpty()) {
            log.warn("Se pidio voz en off pero la solicitud no tiene titulo, descripcion ni llamado a la accion generados.");
            return null;
        }

        if (piperPath.isBlank() || piperVozModeloPath.isBlank()) {
            log.warn("Se pidio voz en off pero piper.path/piper.voz-modelo-path no estan configurados. El video se genera sin narracion.");
            return null;
        }

        Path narracion = directorioTemporal.resolve("narracion-" + UUID.randomUUID() + ".wav");
        List<String> comando = List.of(piperPath, "--model", piperVozModeloPath,
                "--length_scale", String.valueOf(lengthScale),
                "--noise_scale", "0.667", "--noise_w", "0.8", "--sentence_silence", "0.15",
                "--output_file", narracion.toString());

        try {
            ejecutarConEntrada(comando, guion.toString());
        } catch (RuntimeException e) {
            log.warn("No se pudo generar la voz en off ({}). El video se genera sin narracion.", e.getMessage());
            return null;
        }

        return Files.exists(narracion) ? narracion : null;
    }

    private void agregarSiNoVacio(StringBuilder guion, String texto) {
        if (texto != null && !texto.isBlank()) {
            if (!guion.isEmpty()) {
                guion.append(". ");
            }
            guion.append(texto.trim());
        }
    }

    /**
     * Deriva la ruta de ffprobe a partir de ffmpeg.path reemplazando solo el nombre del
     * ejecutable, no toda la cadena: en Windows ffmpeg.path suele incluir una carpeta con
     * "ffmpeg" en el nombre (ej. ".../ffmpeg-9.0.2-full_build/bin/ffmpeg.exe") y un reemplazo
     * de toda la cadena rompe esa carpeta.
     */
    private String ffprobePath() {
        Path ffmpeg = Path.of(ffmpegPath);
        String nombreFfprobe = ffmpeg.getFileName().toString().replace("ffmpeg", "ffprobe");
        Path padre = ffmpeg.getParent();
        return padre != null ? padre.resolve(nombreFfprobe).toString() : nombreFfprobe;
    }

    private double duracionDeArchivo(Path archivo) {
        List<String> comando = List.of(ffprobePath(), "-v", "error", "-show_entries", "format=duration",
                "-of", "default=noprint_wrappers=1:nokey=1", archivo.toString());
        try {
            String salida = ejecutarYCapturar(comando);
            return Double.parseDouble(salida.trim());
        } catch (RuntimeException e) {
            log.warn("No se pudo determinar la duracion de {}, se usan los tiempos por defecto: {}", archivo, e.getMessage());
            return -1;
        }
    }

    /**
     * Decide que musica de fondo usar: la que el usuario subio para esta solicitud tiene
     * prioridad; si no hay o no se puede leer, cae a la configurada en el servidor
     * (ffmpeg.musica-fondo-path) o al tono ambiental por defecto. Devuelve null si no hay
     * ninguna musica disponible (el video se genera sin musica).
     */
    private String resolverMusicaFondo(Path directorioTemporal, SolicitudVideoMarketing solicitud) {
        String musicaPersonalizada = solicitud.getMusicaFondoUrl();
        if (musicaPersonalizada != null && !musicaPersonalizada.isBlank()) {
            try {
                byte[] bytesMusica = almacenamientoArchivosService.leerBytes(musicaPersonalizada);
                Path destino = directorioTemporal.resolve("musica-personalizada" + extensionDeAudio(musicaPersonalizada));
                Files.write(destino, bytesMusica);
                return destino.toString();
            } catch (RuntimeException | IOException e) {
                log.warn("No se pudo leer la musica de fondo personalizada ({}), se usa la musica por defecto: {}",
                        musicaPersonalizada, e.getMessage());
            }
        }

        return !musicaFondoPath.isBlank() && Files.exists(Path.of(musicaFondoPath)) ? musicaFondoPath : null;
    }

    private String extensionDeAudio(String url) {
        String urlLimpia = url.toLowerCase(Locale.ROOT);
        if (urlLimpia.endsWith(".wav")) return ".wav";
        if (urlLimpia.endsWith(".m4a")) return ".m4a";
        if (urlLimpia.endsWith(".ogg")) return ".ogg";
        return ".mp3";
    }

    private Path generarSegmentoFoto(Path directorioTemporal, String url, int indice, String texto, double duracion) throws IOException {
        byte[] bytesFoto = almacenamientoArchivosService.leerBytes(url);
        Path fotoPath = directorioTemporal.resolve("foto-" + indice + ".jpg");
        Files.write(fotoPath, bytesFoto);

        Path segmento = directorioTemporal.resolve("segmento-" + indice + ".mp4");

        String filtro = construirFiltroKenBurns(duracion, indice) + (texto != null && !fontPath.isBlank() ? "," + construirFiltroTexto(texto, duracion) : "");

        List<String> comando = List.of(
                ffmpegPath, "-y",
                "-loop", "1",
                "-i", fotoPath.toString(),
                "-t", String.valueOf(duracion),
                "-r", String.valueOf(FPS),
                "-vf", filtro,
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                segmento.toString());

        ejecutar(comando);

        return segmento;
    }

    private Path generarSegmentoVideo(Path directorioTemporal, String url, int indice, String texto, double duracionMaxima) throws IOException {
        byte[] bytesVideo = almacenamientoArchivosService.leerBytes(url);
        String extension = extensionDeUrl(url);
        Path videoOriginal = directorioTemporal.resolve("fuente-" + indice + extension);
        Files.write(videoOriginal, bytesVideo);

        Path segmento = directorioTemporal.resolve("segmento-" + indice + ".mp4");

        String filtro = construirFiltroEscalado() + ",fps=" + FPS
                + (texto != null && !fontPath.isBlank() ? "," + construirFiltroTexto(texto, duracionMaxima) : "");

        List<String> comando = List.of(
                ffmpegPath, "-y",
                "-i", videoOriginal.toString(),
                "-t", String.valueOf(duracionMaxima),
                "-vf", filtro,
                "-an",
                "-c:v", "libx264",
                "-pix_fmt", "yuv420p",
                segmento.toString());

        ejecutar(comando);

        return segmento;
    }

    private String extensionDeUrl(String url) {
        String urlLimpia = url.toLowerCase(Locale.ROOT);
        if (urlLimpia.endsWith(".mov")) return ".mov";
        if (urlLimpia.endsWith(".webm")) return ".webm";
        if (urlLimpia.endsWith(".mkv")) return ".mkv";
        return ".mp4";
    }

    /**
     * Escala cualquier foto/clip al formato cuadrado del video recortando el sobrante
     * (en vez de agregar franjas negras), para que se vea como un video de venta prolijo
     * y no como un collage de recortes con bordes.
     */
    private String construirFiltroEscalado() {
        return "scale=" + ANCHO + ":" + ALTO + ":force_original_aspect_ratio=increase,"
                + "crop=" + ANCHO + ":" + ALTO;
    }

    /**
     * Efecto "Ken Burns": zoom lento y continuo sobre la foto (alternando un leve paneo
     * hacia la derecha en los indices impares) para que las fotos tengan movimiento propio
     * en vez de quedar estaticas como en un collage.
     */
    private String construirFiltroKenBurns(double duracion, int indice) {
        int lienzo = ANCHO * 2;
        int frames = Math.max(1, (int) Math.round(duracion * FPS));
        boolean panHaciaLaDerecha = indice % 2 == 1;
        String x = panHaciaLaDerecha ? "iw/2-(iw/zoom/2)+iw*0.04" : "iw/2-(iw/zoom/2)";

        return "scale=" + lienzo + ":" + lienzo + ":force_original_aspect_ratio=increase,"
                + "crop=" + lienzo + ":" + lienzo + ","
                + "zoompan=z='min(zoom+0.0020,1.3)':d=" + frames + ":x='" + x + "':y='ih/2-(ih/zoom/2)':"
                + "s=" + ANCHO + "x" + ALTO + ":fps=" + FPS;
    }

    private String construirFiltroTexto(String texto, double duracion) {
        String textoEscapado = escaparParaDrawtext(texto);
        String fontPathEscapado = fontPath.replace("\\", "/").replace(":", "\\:");
        double fundido = Math.min(0.4, duracion / 4);
        String alpha = "if(lt(t\\," + fundido + ")\\,t/" + fundido
                + "\\,if(gt(t\\," + (duracion - fundido) + ")\\,(" + duracion + "-t)/" + fundido + "\\,1))";

        return "drawtext=fontfile='" + fontPathEscapado + "':text='" + textoEscapado + "':"
                + "fontsize=48:fontcolor=white:box=1:boxcolor=black@0.55:boxborderw=20:"
                + "x=(w-text_w)/2:y=h-th-80:line_spacing=8:alpha='" + alpha + "'";
    }

    private String escaparParaDrawtext(String texto) {
        return texto
                .replace("\\", "\\\\")
                .replace(":", "\\:")
                .replace("'", "’")
                .replace("\n", " ");
    }

    /**
     * Une los segmentos con una transicion de fundido (xfade) entre cada par, en vez de
     * pegarlos con cortes secos (concat demuxer), para que el resultado se sienta como un
     * video de venta editado y no como un collage de clips pegados uno detras de otro.
     */
    private Path unirSegmentosConTransicion(Path directorioTemporal, List<Path> segmentos) {
        Path videoFinal = directorioTemporal.resolve("video-final.mp4");

        if (segmentos.size() == 1) {
            ejecutar(List.of(ffmpegPath, "-y", "-i", segmentos.get(0).toString(), "-c", "copy", videoFinal.toString()));
            return videoFinal;
        }

        List<Double> duraciones = segmentos.stream()
                .map(segmento -> {
                    double duracion = duracionDeArchivo(segmento);
                    return duracion > 0 ? duracion : DURACION_POR_FOTO_SEGUNDOS;
                })
                .collect(Collectors.toList());

        List<String> comando = new ArrayList<>();
        comando.add(ffmpegPath);
        comando.add("-y");
        for (Path segmento : segmentos) {
            comando.add("-i");
            comando.add(segmento.toString());
        }

        StringBuilder filtro = new StringBuilder();
        String etiquetaPrevia = "0:v";
        double acumulado = duraciones.get(0);

        for (int i = 1; i < segmentos.size(); i++) {
            double duracionActual = duraciones.get(i);
            double transicion = Math.max(0, Math.min(TRANSICION_SEGUNDOS, Math.min(acumulado, duracionActual) * 0.4));
            double offset = Math.max(0, acumulado - transicion);
            String etiquetaSalida = (i == segmentos.size() - 1) ? "final" : "v" + i;

            filtro.append('[').append(etiquetaPrevia).append("][").append(i).append(":v]xfade=transition=fade:duration=")
                    .append(transicion).append(":offset=").append(offset).append('[').append(etiquetaSalida).append("];");

            acumulado = acumulado + duracionActual - transicion;
            etiquetaPrevia = etiquetaSalida;
        }
        filtro.setLength(filtro.length() - 1);

        comando.add("-filter_complex");
        comando.add(filtro.toString());
        comando.add("-map");
        comando.add("[final]");
        comando.add("-c:v");
        comando.add("libx264");
        comando.add("-pix_fmt");
        comando.add("yuv420p");
        comando.add(videoFinal.toString());

        ejecutar(comando);

        return videoFinal;
    }

    private Path agregarMusicaDeFondo(Path directorioTemporal, Path videoSinAudio, String musicaPath) {
        Path videoConMusica = directorioTemporal.resolve("video-con-musica.mp4");

        List<String> comando = List.of(
                ffmpegPath, "-y",
                "-i", videoSinAudio.toString(),
                "-stream_loop", "-1", "-i", musicaPath,
                "-map", "0:v:0",
                "-map", "1:a:0",
                "-filter:a", "volume=" + VOLUMEN_MUSICA_FONDO,
                "-c:v", "copy",
                "-c:a", "aac",
                "-shortest",
                videoConMusica.toString());

        ejecutar(comando);

        return videoConMusica;
    }

    /**
     * Rellena con silencio la narracion hasta la duracion del video (como string para el filtro
     * de ffmpeg), o null si no se pudo determinar la duracion del video: en ese caso no se puede
     * rellenar con seguridad y se deja el comportamiento anterior (narracion sin relleno).
     * Esto evita que "-shortest" recorte el video cuando la narracion queda mas corta que el
     * ritmo visual (por ejemplo, cuando el usuario pidio un video mas largo que lo que dura
     * leer el guion en voz alta).
     */
    private String duracionParaRelleno(Path videoSinAudio) {
        double duracionVideo = duracionDeArchivo(videoSinAudio);
        return duracionVideo > 0 ? String.valueOf(duracionVideo) : null;
    }

    private Path agregarSoloNarracion(Path directorioTemporal, Path videoSinAudio, Path narracion) {
        Path videoConVoz = directorioTemporal.resolve("video-con-voz.mp4");
        String duracionRelleno = duracionParaRelleno(videoSinAudio);

        List<String> comando;
        if (duracionRelleno != null) {
            comando = List.of(
                    ffmpegPath, "-y",
                    "-i", videoSinAudio.toString(),
                    "-i", narracion.toString(),
                    "-filter_complex", "[1:a]apad=whole_dur=" + duracionRelleno + "[audio]",
                    "-map", "0:v:0",
                    "-map", "[audio]",
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-shortest",
                    videoConVoz.toString());
        } else {
            comando = List.of(
                    ffmpegPath, "-y",
                    "-i", videoSinAudio.toString(),
                    "-i", narracion.toString(),
                    "-map", "0:v:0",
                    "-map", "1:a:0",
                    "-c:v", "copy",
                    "-c:a", "aac",
                    "-shortest",
                    videoConVoz.toString());
        }

        ejecutar(comando);

        return videoConVoz;
    }

    private Path agregarNarracionYMusica(Path directorioTemporal, Path videoSinAudio, Path narracion, String musicaPath) {
        Path videoFinal = directorioTemporal.resolve("video-con-voz-y-musica.mp4");
        String duracionRelleno = duracionParaRelleno(videoSinAudio);

        String filtro = duracionRelleno != null
                ? "[1:a]apad=whole_dur=" + duracionRelleno + "[narracionpad];"
                        + "[2:a]volume=" + VOLUMEN_MUSICA_CON_VOZ + "[musica];"
                        + "[narracionpad][musica]amix=inputs=2:duration=first:dropout_transition=0[audio]"
                : "[2:a]volume=" + VOLUMEN_MUSICA_CON_VOZ + "[musica];[1:a][musica]amix=inputs=2:duration=first:dropout_transition=0[audio]";

        List<String> comando = List.of(
                ffmpegPath, "-y",
                "-i", videoSinAudio.toString(),
                "-i", narracion.toString(),
                "-stream_loop", "-1", "-i", musicaPath,
                "-filter_complex", filtro,
                "-map", "0:v:0",
                "-map", "[audio]",
                "-c:v", "copy",
                "-c:a", "aac",
                "-shortest",
                videoFinal.toString());

        ejecutar(comando);

        return videoFinal;
    }

    private void ejecutar(List<String> comando) {
        try {
            Process proceso = new ProcessBuilder(comando)
                    .redirectErrorStream(true)
                    .start();

            String salida;
            try (var entrada = proceso.inputReader()) {
                salida = entrada.lines().collect(Collectors.joining("\n"));
            }

            boolean termino = proceso.waitFor(60, TimeUnit.SECONDS);
            if (!termino || proceso.exitValue() != 0) {
                proceso.destroyForcibly();
                log.error("ffmpeg fallo o excedio el tiempo limite. Salida:\n{}", salida);
                throw new IllegalStateException("No se pudo generar el video (ffmpeg fallo)");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo ejecutar ffmpeg. Verifica que este instalado (ffmpeg.path)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La generacion del video fue interrumpida", e);
        }
    }

    /**
     * Ejecuta un comando (piper) escribiendole texto por stdin. La salida se drena en un hilo
     * aparte mientras se escribe la entrada, para evitar el deadlock clasico de ProcessBuilder:
     * si el pipe de salida se llena antes de que alguien lo lea, tanto el proceso hijo como este
     * hilo quedan bloqueados para siempre.
     */
    private void ejecutarConEntrada(List<String> comando, String entradaTexto) {
        try {
            Process proceso = new ProcessBuilder(comando)
                    .redirectErrorStream(true)
                    .start();

            StringBuilder salida = new StringBuilder();
            Thread lector = new Thread(() -> {
                try (var entrada = proceso.inputReader()) {
                    entrada.lines().forEach(linea -> salida.append(linea).append('\n'));
                } catch (IOException ignorado) {
                    // el proceso ya termino o el pipe se cerro; no hay nada mas que leer
                }
            });
            lector.start();

            try (var entradaProceso = proceso.getOutputStream()) {
                entradaProceso.write(entradaTexto.getBytes(StandardCharsets.UTF_8));
            }

            boolean termino = proceso.waitFor(60, TimeUnit.SECONDS);
            lector.join(TimeUnit.SECONDS.toMillis(5));

            if (!termino || proceso.exitValue() != 0) {
                proceso.destroyForcibly();
                log.error("piper fallo o excedio el tiempo limite. Salida:\n{}", salida);
                throw new IllegalStateException("No se pudo generar la voz en off (piper fallo)");
            }
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo ejecutar piper. Verifica que este instalado (piper.path)", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("La generacion de la narracion fue interrumpida", e);
        }
    }

    private String ejecutarYCapturar(List<String> comando) {
        try {
            Process proceso = new ProcessBuilder(comando).start();

            String salida;
            try (var entrada = proceso.inputReader()) {
                salida = entrada.lines().collect(Collectors.joining("\n"));
            }

            proceso.waitFor(15, TimeUnit.SECONDS);
            return salida;
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo ejecutar el proceso", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Proceso interrumpido", e);
        }
    }

    private void borrarDirectorio(Path directorio) {
        try (Stream<Path> archivos = Files.walk(directorio)) {
            archivos.sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.deleteIfExists(p);
                } catch (IOException e) {
                    log.warn("No se pudo borrar archivo temporal {}: {}", p, e.getMessage());
                }
            });
        } catch (IOException e) {
            log.warn("No se pudo limpiar el directorio temporal {}: {}", directorio, e.getMessage());
        }
    }
}
