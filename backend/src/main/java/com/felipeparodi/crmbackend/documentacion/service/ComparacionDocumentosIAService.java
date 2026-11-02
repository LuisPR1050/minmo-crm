package com.felipeparodi.crmbackend.documentacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.Base64ImageSource;
import com.anthropic.models.messages.Base64PdfSource;
import com.anthropic.models.messages.ContentBlockParam;
import com.anthropic.models.messages.DocumentBlockParam;
import com.anthropic.models.messages.ImageBlockParam;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.anthropic.models.messages.TextBlockParam;
import com.felipeparodi.crmbackend.documentacion.dto.ComparacionDocumentosIA;
import com.felipeparodi.crmbackend.documentacion.dto.ComparacionDocumentosResponse;
import com.felipeparodi.crmbackend.documentacion.dto.DiferenciaDocumentoIA;
import com.felipeparodi.crmbackend.documentacion.dto.DiferenciaDocumentoResponse;
import com.felipeparodi.crmbackend.documentacion.exception.ServicioIANoDisponibleException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class ComparacionDocumentosIAService {

    private static final String MODELO = "claude-opus-5";

    private static final Set<String> TIPOS_CONTENIDO_PERMITIDOS = Set.of(
            "application/pdf", "image/jpeg", "image/png", "image/webp");

    private final AnthropicClient anthropicClient;

    public ComparacionDocumentosIAService(AnthropicClient anthropicClient) {
        this.anthropicClient = anthropicClient;
    }

    public ComparacionDocumentosResponse comparar(MultipartFile documentoA, MultipartFile documentoB) {
        validarArchivo(documentoA, "A");
        validarArchivo(documentoB, "B");

        List<ContentBlockParam> bloques = new ArrayList<>();
        bloques.add(ContentBlockParam.ofText(TextBlockParam.builder().text(construirPrompt()).build()));
        bloques.add(ContentBlockParam.ofText(TextBlockParam.builder().text("DOCUMENTO A:").build()));
        bloques.add(construirBloqueArchivo(documentoA));
        bloques.add(ContentBlockParam.ofText(TextBlockParam.builder().text("DOCUMENTO B:").build()));
        bloques.add(construirBloqueArchivo(documentoB));

        StructuredMessageCreateParams<ComparacionDocumentosIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(4096L)
                .outputConfig(ComparacionDocumentosIA.class)
                .addUserMessageOfBlockParams(bloques)
                .build();

        ComparacionDocumentosIA resultado;
        try {
            resultado = anthropicClient.messages().create(params).content().stream()
                    .flatMap(block -> block.text().stream())
                    .findFirst()
                    .map(typed -> typed.text())
                    .orElseThrow(() -> new IllegalStateException("La IA no genero una comparacion valida entre los documentos"));
        } catch (com.anthropic.errors.AnthropicException ex) {
            throw new ServicioIANoDisponibleException(
                    "No se pudo comparar los documentos: el servicio de IA no respondio correctamente. "
                            + "Intenta de nuevo en un momento.", ex);
        }

        return mapearAResponse(documentoA, documentoB, resultado);
    }

    private void validarArchivo(MultipartFile archivo, String etiqueta) {
        if (archivo == null || archivo.isEmpty()) {
            throw new IllegalArgumentException("El documento " + etiqueta + " esta vacio o no se recibio");
        }

        String tipoContenido = archivo.getContentType();
        if (tipoContenido == null || !TIPOS_CONTENIDO_PERMITIDOS.contains(tipoContenido)) {
            throw new IllegalArgumentException(
                    "El documento " + etiqueta + " debe ser PDF, JPEG, PNG o WEBP. Tipo recibido: " + tipoContenido);
        }
    }

    private ContentBlockParam construirBloqueArchivo(MultipartFile archivo) {
        String tipoContenido = archivo.getContentType();
        String base64 = codificarBase64(archivo);

        if ("application/pdf".equals(tipoContenido)) {
            return ContentBlockParam.ofDocument(DocumentBlockParam.builder()
                    .source(Base64PdfSource.builder().data(base64).build())
                    .title(archivo.getOriginalFilename())
                    .build());
        }

        return ContentBlockParam.ofImage(ImageBlockParam.builder()
                .source(Base64ImageSource.builder()
                        .data(base64)
                        .mediaType(mediaTypeDeImagen(tipoContenido))
                        .build())
                .build());
    }

    private String codificarBase64(MultipartFile archivo) {
        try {
            return Base64.getEncoder().encodeToString(archivo.getBytes());
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer el documento " + archivo.getOriginalFilename(), e);
        }
    }

    private Base64ImageSource.MediaType mediaTypeDeImagen(String tipoContenido) {
        return switch (tipoContenido) {
            case "image/png" -> Base64ImageSource.MediaType.IMAGE_PNG;
            case "image/webp" -> Base64ImageSource.MediaType.IMAGE_WEBP;
            default -> Base64ImageSource.MediaType.IMAGE_JPEG;
        };
    }

    private String construirPrompt() {
        return "Eres un abogado inmobiliario en Colombia especializado en revisar contratos de compraventa. "
                + "A continuacion te comparto dos versiones de un documento (por ejemplo, dos versiones de una "
                + "promesa de compraventa, un contrato y su otrosi, o dos borradores distintos). Compara el "
                + "DOCUMENTO A contra el DOCUMENTO B y detecta TODAS las diferencias entre ellos: cambios en "
                + "precio, plazos, fechas, partes (comprador, vendedor), forma de pago, condiciones, clausulas "
                + "agregadas o eliminadas, y cualquier otro cambio relevante en el texto. Ignora diferencias "
                + "puramente de formato (saltos de linea, mayusculas, numeracion) que no cambien el sentido del "
                + "texto. No inventes contenido que no este en los documentos.";
    }

    private ComparacionDocumentosResponse mapearAResponse(MultipartFile documentoA, MultipartFile documentoB,
                                                            ComparacionDocumentosIA resultado) {
        List<DiferenciaDocumentoResponse> diferencias = resultado.diferencias().stream()
                .map(this::mapearDiferencia)
                .collect(Collectors.toList());

        return ComparacionDocumentosResponse.builder()
                .nombreDocumentoA(documentoA.getOriginalFilename())
                .nombreDocumentoB(documentoB.getOriginalFilename())
                .resumenGeneral(resultado.resumenGeneral())
                .nivelCambio(resultado.nivelCambio())
                .diferencias(diferencias)
                .fechaComparacion(LocalDateTime.now())
                .build();
    }

    private DiferenciaDocumentoResponse mapearDiferencia(DiferenciaDocumentoIA diferencia) {
        return DiferenciaDocumentoResponse.builder()
                .seccion(diferencia.seccion())
                .contenidoDocumentoA(diferencia.contenidoDocumentoA())
                .contenidoDocumentoB(diferencia.contenidoDocumentoB())
                .tipoCambio(diferencia.tipoCambio())
                .relevancia(diferencia.relevancia())
                .build();
    }
}
