package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.config.AlmacenamientoVideoService;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingResponse;
import com.felipeparodi.crmbackend.marketing.entity.FotoSolicitudVideo;
import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import com.felipeparodi.crmbackend.marketing.entity.TipoOrigenArchivo;
import com.felipeparodi.crmbackend.marketing.entity.VideoFuenteSolicitud;
import com.felipeparodi.crmbackend.marketing.repository.FotoSolicitudVideoRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.repository.VideoFuenteSolicitudRepository;
import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Prueba de integracion real contra el binario de ffmpeg instalado localmente (ver
 * application-local.properties). Si ffmpeg no esta disponible en la ruta configurada, la
 * prueba se salta en vez de fallar (para no romper otras maquinas/CI).
 */
@ExtendWith(MockitoExtension.class)
class VideoSlideshowServiceTest {

    private static final String FFMPEG_PATH =
            "C:/Users/luisp/AppData/Local/Microsoft/WinGet/Packages/Gyan.FFmpeg_Microsoft.Winget.Source_8wekyb3d8bbwe/"
                    + "ffmpeg-9.0.2-full_build/bin/ffmpeg.exe";
    private static final String FONT_PATH = "C:/Windows/Fonts/arialbd.ttf";
    private static final String PIPER_PATH = "C:/Users/luisp/tools/piper/piper/piper.exe";
    private static final String PIPER_VOZ_MODELO_PATH =
            "C:/Users/luisp/tools/piper/voices/es_MX-claude-high/es_MX-claude-high.onnx";

    @Mock
    private SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    @Mock
    private FotoSolicitudVideoRepository fotoSolicitudVideoRepository;
    @Mock
    private VideoFuenteSolicitudRepository videoFuenteSolicitudRepository;
    @Mock
    private SolicitudVideoMarketingService solicitudVideoMarketingService;

    @BeforeEach
    void verificarFfmpegDisponible() {
        Assumptions.assumeTrue(Files.exists(Path.of(FFMPEG_PATH)), "ffmpeg no esta instalado en esta maquina, se omite la prueba");
    }

    @Test
    void generaUnVideoMp4RealAPartirDeDosFotos(@TempDir Path directorioUploads) throws IOException {
        AlmacenamientoArchivosService almacenamientoArchivosService = new AlmacenamientoArchivosService(directorioUploads.toString());
        AlmacenamientoVideoService almacenamientoVideoService = new AlmacenamientoVideoService(
                almacenamientoArchivosService, directorioUploads.toString(), "", "", "", "", "");

        VideoSlideshowService servicio = new VideoSlideshowService(
                solicitudVideoMarketingRepository, fotoSolicitudVideoRepository, videoFuenteSolicitudRepository,
                almacenamientoArchivosService, almacenamientoVideoService, solicitudVideoMarketingService, FFMPEG_PATH, FONT_PATH, "",
                PIPER_PATH, PIPER_VOZ_MODELO_PATH);

        UUID solicitudId = UUID.randomUUID();

        String urlFoto1 = guardarFotoDePrueba(almacenamientoArchivosService, "red");
        String urlFoto2 = guardarFotoDePrueba(almacenamientoArchivosService, "blue");

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder()
                .id(solicitudId)
                .tituloGenerado("Tu nuevo hogar te espera")
                .llamadoAccionTexto("Escribenos ya: 300 123 4567")
                .build();

        FotoSolicitudVideo foto1 = FotoSolicitudVideo.builder().id(UUID.randomUUID()).url(urlFoto1).createdAt(LocalDateTime.now()).build();
        FotoSolicitudVideo foto2 = FotoSolicitudVideo.builder()
                .id(UUID.randomUUID()).url(urlFoto2).createdAt(LocalDateTime.now().plusSeconds(1)).build();

        when(solicitudVideoMarketingRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(foto1, foto2));
        when(videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of());
        when(solicitudVideoMarketingService.buscarPorId(solicitudId))
                .thenReturn(SolicitudVideoMarketingResponse.builder().id(solicitudId).videoGeneradoUrl("placeholder").build());

        SolicitudVideoMarketingResponse resultado = servicio.generarVideo(solicitudId, false);

        assertThat(resultado).isNotNull();
        assertThat(solicitud.getVideoGeneradoUrl()).startsWith("/uploads/videos-marketing/").endsWith(".mp4");

        Path videoGenerado = directorioUploads.resolve(solicitud.getVideoGeneradoUrl().substring("/uploads/".length()));
        assertThat(Files.exists(videoGenerado)).isTrue();
        assertThat(Files.size(videoGenerado)).isGreaterThan(1000L);
    }

    @Test
    void generaUnVideoMezclandoUnaFotoYUnClipDeVideoSubido(@TempDir Path directorioUploads) throws IOException {
        AlmacenamientoArchivosService almacenamientoArchivosService = new AlmacenamientoArchivosService(directorioUploads.toString());
        AlmacenamientoVideoService almacenamientoVideoService = new AlmacenamientoVideoService(
                almacenamientoArchivosService, directorioUploads.toString(), "", "", "", "", "");

        VideoSlideshowService servicio = new VideoSlideshowService(
                solicitudVideoMarketingRepository, fotoSolicitudVideoRepository, videoFuenteSolicitudRepository,
                almacenamientoArchivosService, almacenamientoVideoService, solicitudVideoMarketingService, FFMPEG_PATH, FONT_PATH, "",
                PIPER_PATH, PIPER_VOZ_MODELO_PATH);

        UUID solicitudId = UUID.randomUUID();

        String urlFoto = guardarFotoDePrueba(almacenamientoArchivosService, "green");
        String urlClip = guardarClipDePrueba(almacenamientoVideoService);

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder()
                .id(solicitudId)
                .tituloGenerado("Recorre esta casa por dentro")
                .llamadoAccionTexto("Agenda tu visita hoy")
                .build();

        FotoSolicitudVideo foto = FotoSolicitudVideo.builder().id(UUID.randomUUID()).url(urlFoto).createdAt(LocalDateTime.now()).build();
        VideoFuenteSolicitud clip = VideoFuenteSolicitud.builder()
                .id(UUID.randomUUID()).url(urlClip).tipoOrigen(TipoOrigenArchivo.SUBIDO).createdAt(LocalDateTime.now().plusSeconds(1)).build();

        when(solicitudVideoMarketingRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(foto));
        when(videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(clip));
        when(solicitudVideoMarketingService.buscarPorId(solicitudId))
                .thenReturn(SolicitudVideoMarketingResponse.builder().id(solicitudId).videoGeneradoUrl("placeholder").build());

        servicio.generarVideo(solicitudId, false);

        Path videoGenerado = directorioUploads.resolve(solicitud.getVideoGeneradoUrl().substring("/uploads/".length()));
        assertThat(Files.exists(videoGenerado)).isTrue();
        assertThat(Files.size(videoGenerado)).isGreaterThan(1000L);
    }

    @Test
    void omiteUnaFotoRotaYGeneraElVideoConLasDemas(@TempDir Path directorioUploads) throws IOException {
        AlmacenamientoArchivosService almacenamientoArchivosService = new AlmacenamientoArchivosService(directorioUploads.toString());
        AlmacenamientoVideoService almacenamientoVideoService = new AlmacenamientoVideoService(
                almacenamientoArchivosService, directorioUploads.toString(), "", "", "", "", "");

        VideoSlideshowService servicio = new VideoSlideshowService(
                solicitudVideoMarketingRepository, fotoSolicitudVideoRepository, videoFuenteSolicitudRepository,
                almacenamientoArchivosService, almacenamientoVideoService, solicitudVideoMarketingService, FFMPEG_PATH, FONT_PATH, "",
                PIPER_PATH, PIPER_VOZ_MODELO_PATH);

        UUID solicitudId = UUID.randomUUID();

        String urlFotoValida = guardarFotoDePrueba(almacenamientoArchivosService, "purple");
        String urlFotoRota = "/uploads/inmuebles/no-existe-" + UUID.randomUUID() + ".jpg";

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder()
                .id(solicitudId)
                .tituloGenerado("Titulo de prueba")
                .build();

        FotoSolicitudVideo fotoRota = FotoSolicitudVideo.builder().id(UUID.randomUUID()).url(urlFotoRota).createdAt(LocalDateTime.now()).build();
        FotoSolicitudVideo fotoValida = FotoSolicitudVideo.builder()
                .id(UUID.randomUUID()).url(urlFotoValida).createdAt(LocalDateTime.now().plusSeconds(1)).build();

        when(solicitudVideoMarketingRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(fotoRota, fotoValida));
        when(videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of());
        when(solicitudVideoMarketingService.buscarPorId(solicitudId))
                .thenReturn(SolicitudVideoMarketingResponse.builder().id(solicitudId).videoGeneradoUrl("placeholder").build());

        servicio.generarVideo(solicitudId, false);

        Path videoGenerado = directorioUploads.resolve(solicitud.getVideoGeneradoUrl().substring("/uploads/".length()));
        assertThat(Files.exists(videoGenerado)).isTrue();
        assertThat(Files.size(videoGenerado)).isGreaterThan(1000L);
    }

    @Test
    void mezclaMusicaDeFondoCuandoEstaConfigurada(@TempDir Path directorioUploads) throws IOException {
        AlmacenamientoArchivosService almacenamientoArchivosService = new AlmacenamientoArchivosService(directorioUploads.toString());
        AlmacenamientoVideoService almacenamientoVideoService = new AlmacenamientoVideoService(
                almacenamientoArchivosService, directorioUploads.toString(), "", "", "", "", "");

        Path musicaDePrueba = Files.createTempFile("musica-prueba-", ".mp3");
        ejecutarYEsperar(FFMPEG_PATH, "-y", "-f", "lavfi", "-i", "sine=frequency=440:duration=2", musicaDePrueba.toString());

        VideoSlideshowService servicio = new VideoSlideshowService(
                solicitudVideoMarketingRepository, fotoSolicitudVideoRepository, videoFuenteSolicitudRepository,
                almacenamientoArchivosService, almacenamientoVideoService, solicitudVideoMarketingService, FFMPEG_PATH, FONT_PATH,
                musicaDePrueba.toString(), PIPER_PATH, PIPER_VOZ_MODELO_PATH);

        UUID solicitudId = UUID.randomUUID();
        String urlFoto = guardarFotoDePrueba(almacenamientoArchivosService, "yellow");

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder().id(solicitudId).build();
        FotoSolicitudVideo foto = FotoSolicitudVideo.builder().id(UUID.randomUUID()).url(urlFoto).createdAt(LocalDateTime.now()).build();

        when(solicitudVideoMarketingRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(foto));
        when(videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of());
        when(solicitudVideoMarketingService.buscarPorId(solicitudId))
                .thenReturn(SolicitudVideoMarketingResponse.builder().id(solicitudId).videoGeneradoUrl("placeholder").build());

        try {
            servicio.generarVideo(solicitudId, false);

            Path videoGenerado = directorioUploads.resolve(solicitud.getVideoGeneradoUrl().substring("/uploads/".length()));
            assertThat(Files.exists(videoGenerado)).isTrue();
            assertThat(tieneStreamDeAudio(videoGenerado)).isTrue();
        } finally {
            Files.deleteIfExists(musicaDePrueba);
        }
    }

    @Test
    void generaVideoConVozOffCuandoSeSolicita(@TempDir Path directorioUploads) throws IOException {
        Assumptions.assumeTrue(Files.exists(Path.of(PIPER_PATH)) && Files.exists(Path.of(PIPER_VOZ_MODELO_PATH)),
                "piper no esta instalado en esta maquina, se omite la prueba");

        AlmacenamientoArchivosService almacenamientoArchivosService = new AlmacenamientoArchivosService(directorioUploads.toString());
        AlmacenamientoVideoService almacenamientoVideoService = new AlmacenamientoVideoService(
                almacenamientoArchivosService, directorioUploads.toString(), "", "", "", "", "");

        VideoSlideshowService servicio = new VideoSlideshowService(
                solicitudVideoMarketingRepository, fotoSolicitudVideoRepository, videoFuenteSolicitudRepository,
                almacenamientoArchivosService, almacenamientoVideoService, solicitudVideoMarketingService, FFMPEG_PATH, FONT_PATH, "",
                PIPER_PATH, PIPER_VOZ_MODELO_PATH);

        UUID solicitudId = UUID.randomUUID();
        String urlFoto = guardarFotoDePrueba(almacenamientoArchivosService, "orange");

        SolicitudVideoMarketing solicitud = SolicitudVideoMarketing.builder()
                .id(solicitudId)
                .tituloGenerado("Apartamento con vista panoramica")
                .descripcionGenerada("Tres habitaciones, dos banos, balcon amplio y zona social en el primer piso")
                .llamadoAccionTexto("Escribenos ya para agendar tu visita")
                .build();

        FotoSolicitudVideo foto = FotoSolicitudVideo.builder().id(UUID.randomUUID()).url(urlFoto).createdAt(LocalDateTime.now()).build();

        when(solicitudVideoMarketingRepository.findById(solicitudId)).thenReturn(Optional.of(solicitud));
        when(fotoSolicitudVideoRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of(foto));
        when(videoFuenteSolicitudRepository.findBySolicitudVideoId(solicitudId)).thenReturn(List.of());
        when(solicitudVideoMarketingService.buscarPorId(solicitudId))
                .thenReturn(SolicitudVideoMarketingResponse.builder().id(solicitudId).videoGeneradoUrl("placeholder").build());

        servicio.generarVideo(solicitudId, true);

        Path videoGenerado = directorioUploads.resolve(solicitud.getVideoGeneradoUrl().substring("/uploads/".length()));
        assertThat(Files.exists(videoGenerado)).isTrue();
        assertThat(tieneStreamDeAudio(videoGenerado)).isTrue();
    }

    private boolean tieneStreamDeAudio(Path video) throws IOException {
        Path ffprobe = Path.of(FFMPEG_PATH).getParent().resolve("ffprobe.exe");
        StringBuilder salida = new StringBuilder();
        try {
            Process proceso = new ProcessBuilder(ffprobe.toString(), "-i", video.toString()).redirectErrorStream(true).start();
            try (var entrada = proceso.inputReader()) {
                entrada.lines().forEach(linea -> salida.append(linea).append('\n'));
            }
            proceso.waitFor(15, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
        return salida.toString().contains("Audio:");
    }

    private String guardarFotoDePrueba(AlmacenamientoArchivosService almacenamiento, String color) throws IOException {
        Path jpgTemporal = Files.createTempFile("foto-prueba-", ".jpg");
        try {
            ejecutarYEsperar(FFMPEG_PATH, "-y", "-f", "lavfi", "-i", "color=c=" + color + ":s=640x480", "-frames:v", "1",
                    jpgTemporal.toString());

            byte[] bytes = Files.readAllBytes(jpgTemporal);
            org.springframework.mock.web.MockMultipartFile archivo =
                    new org.springframework.mock.web.MockMultipartFile("archivo", "foto.jpg", "image/jpeg", bytes);

            return almacenamiento.guardarImagen("fotos-prueba", archivo);
        } finally {
            Files.deleteIfExists(jpgTemporal);
        }
    }

    private String guardarClipDePrueba(AlmacenamientoVideoService almacenamientoVideoService) throws IOException {
        Path clipTemporal = Files.createTempFile("clip-prueba-", ".mp4");
        try {
            ejecutarYEsperar(FFMPEG_PATH, "-y", "-f", "lavfi", "-i", "testsrc=size=320x240:duration=2",
                    "-c:v", "libx264", "-pix_fmt", "yuv420p", "-t", "2", clipTemporal.toString());

            byte[] bytes = Files.readAllBytes(clipTemporal);
            org.springframework.mock.web.MockMultipartFile archivo =
                    new org.springframework.mock.web.MockMultipartFile("archivo", "clip.mp4", "video/mp4", bytes);

            return almacenamientoVideoService.guardarVideo("videos-prueba", archivo);
        } finally {
            Files.deleteIfExists(clipTemporal);
        }
    }

    private void ejecutarYEsperar(String... comando) throws IOException {
        try {
            Process proceso = new ProcessBuilder(comando).redirectErrorStream(true).start();
            try (var entrada = proceso.inputReader()) {
                entrada.lines().forEach(linea -> { });
            }
            proceso.waitFor(30, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException(e);
        }
    }
}
