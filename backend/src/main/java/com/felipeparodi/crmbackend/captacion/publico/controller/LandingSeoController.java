package com.felipeparodi.crmbackend.captacion.publico.controller;

import com.felipeparodi.crmbackend.captacion.publico.dto.LandingFotoResponse;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingInmuebleResponse;
import com.felipeparodi.crmbackend.captacion.publico.service.LandingInmuebleService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

/**
 * Cuando una red social (WhatsApp, Facebook, Twitter/X, LinkedIn, Slack, Telegram) o un buscador
 * entra a /p/{id} para generar la vista previa del link, no ejecuta JavaScript — solo lee las
 * etiquetas <meta> del HTML que recibe. Como el resto de la app es un SPA (React Router), esa
 * vista previa siempre mostraria el titulo/imagen generico del index.html, nunca la foto o el
 * titulo real del inmueble.
 * <p>
 * Este controller detecta esos crawlers por su User-Agent y les devuelve un HTML minimo con las
 * etiquetas Open Graph correctas para ESE inmueble. Un navegador real (sin ese User-Agent) sigue
 * recibiendo el index.html normal del SPA, servido por {@link com.felipeparodi.crmbackend.config.SpaWebConfig}.
 */
@RestController
public class LandingSeoController {

    private static final List<String> PATRONES_USER_AGENT_CRAWLER = List.of(
            "facebookexternalhit", "facebot", "whatsapp", "twitterbot", "linkedinbot",
            "slackbot", "telegrambot", "discordbot", "pinterest", "googlebot", "bingbot",
            "whatsapp", "vkshare", "skypeuripreview");

    private final LandingInmuebleService landingInmuebleService;

    public LandingSeoController(LandingInmuebleService landingInmuebleService) {
        this.landingInmuebleService = landingInmuebleService;
    }

    @GetMapping(value = "/p/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> landingPorCrawler(@PathVariable UUID id, HttpServletRequest request) {
        String userAgent = request.getHeader(HttpHeaders.USER_AGENT);
        boolean esCrawler = userAgent != null && esCrawlerConocido(userAgent.toLowerCase(Locale.ROOT));

        if (!esCrawler) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(leerIndexHtml());
        }

        LandingInmuebleResponse inmueble;
        try {
            inmueble = landingInmuebleService.obtener(id);
        } catch (RuntimeException e) {
            return ResponseEntity.ok()
                    .contentType(MediaType.TEXT_HTML)
                    .body(leerIndexHtml());
        }

        String urlBase = ServletUriComponentsBuilder.fromRequestUri(request)
                .replacePath(null)
                .replaceQuery(null)
                .build()
                .toUriString();
        String urlPagina = urlBase + "/p/" + id;

        String titulo = construirTitulo(inmueble);
        String descripcion = construirDescripcion(inmueble);
        String urlImagen = construirUrlImagen(inmueble, urlBase);

        return ResponseEntity.ok()
                .contentType(MediaType.TEXT_HTML)
                .body(construirHtml(titulo, descripcion, urlImagen, urlPagina));
    }

    private boolean esCrawlerConocido(String userAgentMinuscula) {
        return PATRONES_USER_AGENT_CRAWLER.stream().anyMatch(userAgentMinuscula::contains);
    }

    private String construirTitulo(LandingInmuebleResponse inmueble) {
        String ubicacion = inmueble.getZona() != null ? inmueble.getZona() : inmueble.getCiudad();
        String tipo = inmueble.getTipoPropiedad() != null ? inmueble.getTipoPropiedad() : "Propiedad";
        String base = ubicacion != null ? tipo + " en " + ubicacion : tipo;
        return base + " — MInmo";
    }

    private String construirDescripcion(LandingInmuebleResponse inmueble) {
        String texto = inmueble.getGanchoEmocional() != null && !inmueble.getGanchoEmocional().isBlank()
                ? inmueble.getGanchoEmocional()
                : inmueble.getDescripcionPublica();
        if (texto == null || texto.isBlank()) {
            return "Conoce esta propiedad y agenda tu visita.";
        }
        return texto.length() > 200 ? texto.substring(0, 197) + "..." : texto;
    }

    private String construirUrlImagen(LandingInmuebleResponse inmueble, String urlBase) {
        if (inmueble.getFotos() == null || inmueble.getFotos().isEmpty()) {
            return null;
        }
        LandingFotoResponse principal = inmueble.getFotos().stream()
                .filter(LandingFotoResponse::isEsPrincipal)
                .findFirst()
                .orElse(inmueble.getFotos().get(0));
        return urlBase + principal.getUrl();
    }

    private String construirHtml(String titulo, String descripcion, String urlImagen, String urlPagina) {
        String tituloEscapado = HtmlUtils.htmlEscape(titulo);
        String descripcionEscapada = HtmlUtils.htmlEscape(descripcion);
        String urlPaginaEscapada = HtmlUtils.htmlEscape(urlPagina);

        StringBuilder html = new StringBuilder();
        html.append("<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\">");
        html.append("<title>").append(tituloEscapado).append("</title>");
        html.append("<meta name=\"description\" content=\"").append(descripcionEscapada).append("\">");
        html.append("<meta property=\"og:type\" content=\"website\">");
        html.append("<meta property=\"og:title\" content=\"").append(tituloEscapado).append("\">");
        html.append("<meta property=\"og:description\" content=\"").append(descripcionEscapada).append("\">");
        html.append("<meta property=\"og:url\" content=\"").append(urlPaginaEscapada).append("\">");
        if (urlImagen != null) {
            html.append("<meta property=\"og:image\" content=\"").append(HtmlUtils.htmlEscape(urlImagen)).append("\">");
            html.append("<meta name=\"twitter:card\" content=\"summary_large_image\">");
        } else {
            html.append("<meta name=\"twitter:card\" content=\"summary\">");
        }
        html.append("<meta name=\"twitter:title\" content=\"").append(tituloEscapado).append("\">");
        html.append("<meta name=\"twitter:description\" content=\"").append(descripcionEscapada).append("\">");
        html.append("<link rel=\"canonical\" href=\"").append(urlPaginaEscapada).append("\">");
        html.append("</head><body><h1>").append(tituloEscapado).append("</h1><p>").append(descripcionEscapada)
                .append("</p></body></html>");
        return html.toString();
    }

    private String leerIndexHtml() {
        try {
            return StreamUtils.copyToString(
                    new ClassPathResource("/static/index.html").getInputStream(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("No se pudo leer index.html", e);
        }
    }
}
