package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PdfDocumentoServiceTest {

    @Test
    void generaUnPdfValidoYLoGuardaEnElDirectorioDeSubidas(@TempDir Path directorioTemporal) throws IOException {
        AlmacenamientoArchivosService almacenamiento = new AlmacenamientoArchivosService(directorioTemporal.toString());
        PdfDocumentoService pdfDocumentoService = new PdfDocumentoService(almacenamiento);

        String texto = "CLAUSULA PRIMERA: Objeto del contrato.\n"
                + "El presente contrato de corretaje se rige por las normas colombianas.\n"
                + "Acentos y ñ para verificar codificación.";

        String urlPublica = pdfDocumentoService.generarYGuardar("contratos-corretaje-pdf",
                "Contrato de corretaje CC-0001", texto, null);

        assertThat(urlPublica).startsWith("/uploads/contratos-corretaje-pdf/").endsWith(".pdf");

        Path archivoGenerado = directorioTemporal.resolve(urlPublica.substring("/uploads/".length()));
        assertThat(Files.exists(archivoGenerado)).isTrue();

        byte[] contenido = Files.readAllBytes(archivoGenerado);
        assertThat(contenido.length).isGreaterThan(100);
        assertThat(new String(contenido, 0, 5, java.nio.charset.StandardCharsets.US_ASCII)).isEqualTo("%PDF-");
    }

    @Test
    void alRegenerarElPdfEliminaElArchivoAnterior(@TempDir Path directorioTemporal) throws IOException {
        AlmacenamientoArchivosService almacenamiento = new AlmacenamientoArchivosService(directorioTemporal.toString());
        PdfDocumentoService pdfDocumentoService = new PdfDocumentoService(almacenamiento);

        String urlAnterior = pdfDocumentoService.generarYGuardar("otrosis-promesa-pdf", "Otrosi O-0001", "Texto inicial", null);
        Path archivoAnterior = directorioTemporal.resolve(urlAnterior.substring("/uploads/".length()));
        assertThat(Files.exists(archivoAnterior)).isTrue();

        String urlNueva = pdfDocumentoService.generarYGuardar("otrosis-promesa-pdf", "Otrosi O-0001", "Texto actualizado", urlAnterior);

        assertThat(Files.exists(archivoAnterior)).isFalse();
        Path archivoNuevo = directorioTemporal.resolve(urlNueva.substring("/uploads/".length()));
        assertThat(Files.exists(archivoNuevo)).isTrue();
    }
}
