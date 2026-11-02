package com.felipeparodi.crmbackend.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;
import java.util.UUID;
import javax.imageio.ImageIO;

@Service
public class AlmacenamientoArchivosService {

    private static final Logger log = LoggerFactory.getLogger(AlmacenamientoArchivosService.class);

    private static final Set<String> TIPOS_CONTENIDO_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp");

    private static final int LADO_MAXIMO_PX = 1920;

    private static final Set<String> TIPOS_CONTENIDO_DOCUMENTO_PERMITIDOS = Set.of(
            "image/jpeg", "image/png", "image/webp", "application/pdf");

    private final Path directorioBase;

    public AlmacenamientoArchivosService(@Value("${storage.uploads-dir}") String directorioUploads) {
        this.directorioBase = Path.of(directorioUploads).toAbsolutePath().normalize();
        try {
            Files.createDirectories(directorioBase);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo crear el directorio de almacenamiento: " + directorioBase, e);
        }
    }

    /**
     * Guarda una imagen dentro de una subcarpeta y devuelve la ruta publica
     * (servida por AlmacenamientoWebConfig) con la que se puede acceder al archivo.
     */
    public String guardarImagen(String subcarpeta, MultipartFile archivo) {
        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !TIPOS_CONTENIDO_PERMITIDOS.contains(tipoContenido)) {
            throw new IllegalArgumentException(
                    "Solo se permiten imagenes JPEG, PNG o WEBP. Tipo recibido: " + tipoContenido);
        }

        String extension = switch (tipoContenido) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            default -> ".jpg";
        };

        String nombreArchivo = UUID.randomUUID() + extension;

        try {
            Path directorioDestino = directorioBase.resolve(subcarpeta).normalize();
            Files.createDirectories(directorioDestino);

            Path destino = directorioDestino.resolve(nombreArchivo).normalize();
            if (!destino.startsWith(directorioBase)) {
                throw new IllegalArgumentException("Ruta de archivo invalida");
            }

            byte[] bytes = redimensionarSiEsNecesario(archivo.getBytes(), tipoContenido);
            Files.write(destino, bytes);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo", e);
        }

        return "/uploads/" + subcarpeta + "/" + nombreArchivo;
    }

    /**
     * Reduce fotos muy grandes (tipicas de camaras de celular) para que la landing publica y el
     * resto de la app carguen mas rapido. Si la imagen ya es pequena, o el formato no lo puede
     * leer/escribir ImageIO (ej. WEBP, que el JDK no soporta sin plugins), se deja tal cual: esto
     * nunca debe romper una subida que hoy funciona.
     */
    private byte[] redimensionarSiEsNecesario(byte[] bytesOriginales, String tipoContenido) {
        try {
            BufferedImage imagen = ImageIO.read(new ByteArrayInputStream(bytesOriginales));
            if (imagen == null) {
                return bytesOriginales;
            }

            int ancho = imagen.getWidth();
            int alto = imagen.getHeight();
            int ladoMayor = Math.max(ancho, alto);
            if (ladoMayor <= LADO_MAXIMO_PX) {
                return bytesOriginales;
            }

            double escala = (double) LADO_MAXIMO_PX / ladoMayor;
            int nuevoAncho = Math.max(1, Math.round((float) (ancho * escala)));
            int nuevoAlto = Math.max(1, Math.round((float) (alto * escala)));

            BufferedImage redimensionada = new BufferedImage(nuevoAncho, nuevoAlto,
                    "image/png".equals(tipoContenido) ? BufferedImage.TYPE_INT_ARGB : BufferedImage.TYPE_INT_RGB);
            Graphics2D g = redimensionada.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
            g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g.drawImage(imagen, 0, 0, nuevoAncho, nuevoAlto, null);
            g.dispose();

            String formatoImageIO = "image/png".equals(tipoContenido) ? "png" : "jpg";
            ByteArrayOutputStream salida = new ByteArrayOutputStream();
            boolean escrito = ImageIO.write(redimensionada, formatoImageIO, salida);
            return escrito ? salida.toByteArray() : bytesOriginales;
        } catch (IOException | RuntimeException e) {
            log.warn("No se pudo redimensionar la imagen, se guarda el original: {}", e.getMessage());
            return bytesOriginales;
        }
    }

    /**
     * Guarda un documento (imagen o PDF) dentro de una subcarpeta y devuelve la ruta publica
     * (servida por AlmacenamientoWebConfig) con la que se puede acceder al archivo.
     */
    public String guardarDocumento(String subcarpeta, MultipartFile archivo) {
        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !TIPOS_CONTENIDO_DOCUMENTO_PERMITIDOS.contains(tipoContenido)) {
            throw new IllegalArgumentException(
                    "Solo se permiten imagenes JPEG, PNG, WEBP o PDF. Tipo recibido: " + tipoContenido);
        }

        String extension = switch (tipoContenido) {
            case "image/png" -> ".png";
            case "image/webp" -> ".webp";
            case "application/pdf" -> ".pdf";
            default -> ".jpg";
        };

        String nombreArchivo = UUID.randomUUID() + extension;

        try {
            Path directorioDestino = directorioBase.resolve(subcarpeta).normalize();
            Files.createDirectories(directorioDestino);

            Path destino = directorioDestino.resolve(nombreArchivo).normalize();
            if (!destino.startsWith(directorioBase)) {
                throw new IllegalArgumentException("Ruta de archivo invalida");
            }

            archivo.transferTo(destino);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo guardar el archivo", e);
        }

        return "/uploads/" + subcarpeta + "/" + nombreArchivo;
    }

    public byte[] leerBytes(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/uploads/")) {
            throw new IllegalArgumentException("Ruta de archivo invalida: " + rutaPublica);
        }

        Path ruta = directorioBase.resolve(rutaPublica.substring("/uploads/".length())).normalize();
        if (!ruta.startsWith(directorioBase)) {
            throw new IllegalArgumentException("Ruta de archivo invalida: " + rutaPublica);
        }

        try {
            return Files.readAllBytes(ruta);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el archivo: " + ruta, e);
        }
    }

    public void eliminarPorRutaPublica(String rutaPublica) {
        if (rutaPublica == null || !rutaPublica.startsWith("/uploads/")) {
            return;
        }

        Path ruta = directorioBase.resolve(rutaPublica.substring("/uploads/".length())).normalize();
        if (!ruta.startsWith(directorioBase)) {
            return;
        }

        try {
            Files.deleteIfExists(ruta);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo eliminar el archivo: " + ruta, e);
        }
    }
}
