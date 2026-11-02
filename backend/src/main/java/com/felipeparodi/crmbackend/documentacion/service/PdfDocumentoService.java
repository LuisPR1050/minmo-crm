package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.documentacion.util.ArchivoEnMemoria;
import com.lowagie.text.Document;
import com.lowagie.text.Font;
import com.lowagie.text.FontFactory;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.UncheckedIOException;

/**
 * Genera PDFs a partir del texto de documentos redactados por IA (contratos, promesas,
 * otrosis) y los guarda reutilizando el almacenamiento de archivos ya existente.
 */
@Service
public class PdfDocumentoService {

    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public PdfDocumentoService(AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public String generarYGuardar(String subcarpeta, String titulo, String texto, String pdfUrlAnterior) {
        byte[] pdf = generarPdf(titulo, texto);
        ArchivoEnMemoria archivo = new ArchivoEnMemoria(titulo + ".pdf", "application/pdf", pdf);

        String nuevaUrl = almacenamientoArchivosService.guardarDocumento(subcarpeta, archivo);

        if (pdfUrlAnterior != null && !pdfUrlAnterior.isBlank()) {
            almacenamientoArchivosService.eliminarPorRutaPublica(pdfUrlAnterior);
        }

        return nuevaUrl;
    }

    private byte[] generarPdf(String titulo, String texto) {
        try (ByteArrayOutputStream salida = new ByteArrayOutputStream()) {
            Document documento = new Document(PageSize.LETTER, 50, 50, 50, 50);
            PdfWriter.getInstance(documento, salida);
            documento.open();

            Font fuenteTitulo = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14);
            Font fuenteCuerpo = FontFactory.getFont(FontFactory.HELVETICA, 11);

            documento.add(new Paragraph(titulo, fuenteTitulo));
            documento.add(new Paragraph(" "));

            for (String linea : texto.split("\n")) {
                documento.add(new Paragraph(linea, fuenteCuerpo));
            }

            documento.close();
            return salida.toByteArray();
        } catch (Exception ex) {
            throw new UncheckedIOException("No se pudo generar el PDF: " + ex.getMessage(),
                    new java.io.IOException(ex));
        }
    }
}
