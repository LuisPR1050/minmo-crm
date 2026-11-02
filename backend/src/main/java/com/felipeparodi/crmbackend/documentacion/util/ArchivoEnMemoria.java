package com.felipeparodi.crmbackend.documentacion.util;

import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Adaptador de MultipartFile para contenido generado en memoria (por ejemplo, un PDF
 * generado por IA), de modo que pueda reutilizarse el mismo flujo de almacenamiento
 * que usan las subidas de archivos hechas por el usuario.
 */
public class ArchivoEnMemoria implements MultipartFile {

    private final String nombreOriginal;
    private final String tipoContenido;
    private final byte[] contenido;

    public ArchivoEnMemoria(String nombreOriginal, String tipoContenido, byte[] contenido) {
        this.nombreOriginal = nombreOriginal;
        this.tipoContenido = tipoContenido;
        this.contenido = contenido;
    }

    @Override
    public String getName() {
        return nombreOriginal;
    }

    @Override
    public String getOriginalFilename() {
        return nombreOriginal;
    }

    @Override
    public String getContentType() {
        return tipoContenido;
    }

    @Override
    public boolean isEmpty() {
        return contenido == null || contenido.length == 0;
    }

    @Override
    public long getSize() {
        return contenido.length;
    }

    @Override
    public byte[] getBytes() throws IOException {
        return contenido;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new ByteArrayInputStream(contenido);
    }

    @Override
    public void transferTo(Path dest) throws IOException, IllegalStateException {
        Files.write(dest, contenido);
    }

    @Override
    public void transferTo(java.io.File dest) throws IOException, IllegalStateException {
        Files.write(dest.toPath(), contenido);
    }
}
