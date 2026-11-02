package com.felipeparodi.crmbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;

/**
 * Guarda videos de inmuebles en Cloudflare R2 (compatible con S3) cuando hay credenciales
 * configuradas; si no, cae al mismo volumen local que usa el resto de archivos subidos
 * (servido via /uploads/**, detras de autenticacion, igual que hoy).
 */
@Service
public class AlmacenamientoVideoService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenamientoVideoService.class);

    private static final Set<String> TIPOS_CONTENIDO_VIDEO_PERMITIDOS = Set.of(
            "video/mp4", "video/quicktime", "video/webm", "video/x-matroska");

    private static final Set<String> TIPOS_CONTENIDO_AUDIO_PERMITIDOS = Set.of(
            "audio/mpeg", "audio/mp3", "audio/wav", "audio/x-wav", "audio/mp4", "audio/x-m4a", "audio/ogg");

    private final AlmacenamientoArchivosService almacenamientoArchivosService;
    private final Path directorioBase;
    private final String publicBaseUrl;
    private final boolean habilitadoR2;
    private final String bucket;
    private S3Client s3Client;

    @Autowired
    public AlmacenamientoVideoService(AlmacenamientoArchivosService almacenamientoArchivosService,
                                       @Value("${storage.uploads-dir}") String directorioUploads,
                                       @Value("${storage.r2.account-id:}") String accountId,
                                       @Value("${storage.r2.access-key-id:}") String accessKeyId,
                                       @Value("${storage.r2.secret-access-key:}") String secretAccessKey,
                                       @Value("${storage.r2.bucket:}") String bucket,
                                       @Value("${storage.r2.public-base-url:}") String publicBaseUrl) {
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.directorioBase = Path.of(directorioUploads).toAbsolutePath().normalize();
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl != null ? publicBaseUrl.replaceAll("/+$", "") : "";
        this.habilitadoR2 = !accountId.isBlank() && !accessKeyId.isBlank()
                && !secretAccessKey.isBlank() && !bucket.isBlank() && !this.publicBaseUrl.isBlank();

        if (this.habilitadoR2) {
            this.s3Client = S3Client.builder()
                    .endpointOverride(URI.create("https://" + accountId + ".r2.cloudflarestorage.com"))
                    .region(Region.of("auto"))
                    .credentialsProvider(StaticCredentialsProvider.create(
                            AwsBasicCredentials.create(accessKeyId, secretAccessKey)))
                    .build();
        } else {
            log.warn("Cloudflare R2 no esta configurado (storage.r2.account-id / access-key-id / "
                    + "secret-access-key / bucket). Los videos se guardaran en el volumen local.");
        }
    }

    /**
     * Constructor visible solo para pruebas: permite inyectar un S3Client (mock) ya construido
     * en vez de que el servicio arme uno real contra Cloudflare R2.
     */
    AlmacenamientoVideoService(AlmacenamientoArchivosService almacenamientoArchivosService,
                                String directorioUploads, String bucket, String publicBaseUrl, S3Client s3Client) {
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.directorioBase = Path.of(directorioUploads).toAbsolutePath().normalize();
        this.bucket = bucket;
        this.publicBaseUrl = publicBaseUrl.replaceAll("/+$", "");
        this.habilitadoR2 = true;
        this.s3Client = s3Client;
    }

    public boolean usaR2() {
        return habilitadoR2;
    }

    public String guardarVideo(String subcarpeta, MultipartFile archivo) {
        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !TIPOS_CONTENIDO_VIDEO_PERMITIDOS.contains(tipoContenido)) {
            throw new IllegalArgumentException(
                    "Solo se permiten videos MP4, MOV, WEBM o MKV. Tipo recibido: " + tipoContenido);
        }

        String extension = switch (tipoContenido) {
            case "video/quicktime" -> ".mov";
            case "video/webm" -> ".webm";
            case "video/x-matroska" -> ".mkv";
            default -> ".mp4";
        };
        String nombreArchivo = UUID.randomUUID() + extension;

        if (habilitadoR2) {
            return guardarEnR2(subcarpeta, nombreArchivo, tipoContenido, archivo);
        }
        return guardarEnDiscoLocal(subcarpeta, nombreArchivo, archivo);
    }

    /**
     * Guarda un archivo de audio (musica de fondo subida por el usuario para un video de
     * marketing), con el mismo mecanismo de almacenamiento (R2 o disco local) que los videos.
     */
    public String guardarAudio(String subcarpeta, MultipartFile archivo) {
        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !TIPOS_CONTENIDO_AUDIO_PERMITIDOS.contains(tipoContenido)) {
            throw new IllegalArgumentException(
                    "Solo se permiten archivos de audio MP3, WAV, M4A u OGG. Tipo recibido: " + tipoContenido);
        }

        String extension = switch (tipoContenido) {
            case "audio/wav", "audio/x-wav" -> ".wav";
            case "audio/mp4", "audio/x-m4a" -> ".m4a";
            case "audio/ogg" -> ".ogg";
            default -> ".mp3";
        };
        String nombreArchivo = UUID.randomUUID() + extension;

        if (habilitadoR2) {
            return guardarEnR2(subcarpeta, nombreArchivo, tipoContenido, archivo);
        }
        return guardarEnDiscoLocal(subcarpeta, nombreArchivo, archivo);
    }

    public void eliminarVideo(String urlPublica) {
        if (urlPublica == null || urlPublica.isBlank()) {
            return;
        }
        if (habilitadoR2 && urlPublica.startsWith(publicBaseUrl) && !publicBaseUrl.isBlank()) {
            String key = urlPublica.substring(publicBaseUrl.length() + 1);
            s3Client.deleteObject(DeleteObjectRequest.builder().bucket(bucket).key(key).build());
            return;
        }
        almacenamientoArchivosService.eliminarPorRutaPublica(urlPublica);
    }

    /**
     * Resuelve la ruta local real de un video guardado en el volumen (no en R2), para que el
     * proxy publico de streaming pueda leerlo en modo random-access (soporte de Range) sin
     * cargar el archivo completo en memoria.
     */
    public Path resolverRutaLocal(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/uploads/")) {
            throw new IllegalArgumentException("Ruta de video invalida: " + rutaPublica);
        }

        Path ruta = directorioBase.resolve(rutaPublica.substring("/uploads/".length())).normalize();
        if (!ruta.startsWith(directorioBase)) {
            throw new IllegalArgumentException("Ruta de video invalida: " + rutaPublica);
        }
        return ruta;
    }

    private String guardarEnR2(String subcarpeta, String nombreArchivo, String tipoContenido, MultipartFile archivo) {
        String key = subcarpeta + "/" + nombreArchivo;
        try {
            s3Client.putObject(
                    PutObjectRequest.builder().bucket(bucket).key(key).contentType(tipoContenido).build(),
                    RequestBody.fromInputStream(archivo.getInputStream(), archivo.getSize()));
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo subir el video a R2", e);
        }
        return publicBaseUrl + "/" + key;
    }

    private String guardarEnDiscoLocal(String subcarpeta, String nombreArchivo, MultipartFile archivo) {
        try {
            Path directorioDestino = directorioBase.resolve(subcarpeta).normalize();
            Files.createDirectories(directorioDestino);

            Path destino = directorioDestino.resolve(nombreArchivo).normalize();
            if (!destino.startsWith(directorioBase)) {
                throw new IllegalArgumentException("Ruta de archivo invalida");
            }

            archivo.transferTo(destino);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el video", e);
        }

        return "/uploads/" + subcarpeta + "/" + nombreArchivo;
    }
}
