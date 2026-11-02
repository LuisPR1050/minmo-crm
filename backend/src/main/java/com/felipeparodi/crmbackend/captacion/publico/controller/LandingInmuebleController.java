package com.felipeparodi.crmbackend.captacion.publico.controller;

import com.felipeparodi.crmbackend.captacion.publico.dto.LandingInmuebleResponse;
import com.felipeparodi.crmbackend.captacion.publico.dto.LandingLeadRequest;
import com.felipeparodi.crmbackend.captacion.publico.service.LandingInmuebleService;
import com.felipeparodi.crmbackend.captacion.publico.service.LimitadorDeFrecuenciaLeads;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourceRegion;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpRange;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.UUID;

/**
 * Endpoints publicos (sin autenticacion) para la landing page de un inmueble:
 * los ve el cliente final a traves de un link compartido, no un usuario del CRM.
 * Solo expone inmuebles con landingActiva=true y campos no sensibles (ver
 * LandingInmuebleService y LandingInmuebleResponse).
 */
@RestController
@RequestMapping("/api/public/inmuebles")
public class LandingInmuebleController {

    private static final long TAMANO_FRAGMENTO = 1_000_000L;

    private final LandingInmuebleService landingInmuebleService;
    private final LimitadorDeFrecuenciaLeads limitadorDeFrecuenciaLeads;

    public LandingInmuebleController(LandingInmuebleService landingInmuebleService,
                                      LimitadorDeFrecuenciaLeads limitadorDeFrecuenciaLeads) {
        this.landingInmuebleService = landingInmuebleService;
        this.limitadorDeFrecuenciaLeads = limitadorDeFrecuenciaLeads;
    }

    @GetMapping("/{id}")
    public LandingInmuebleResponse obtener(@PathVariable UUID id) {
        return landingInmuebleService.obtener(id);
    }

    @PostMapping("/{id}/leads")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void crearLead(@PathVariable UUID id, @Valid @RequestBody LandingLeadRequest request,
                           HttpServletRequest httpRequest) {
        if (!limitadorDeFrecuenciaLeads.permitir(resolverIpCliente(httpRequest))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiadas solicitudes. Intenta de nuevo en unos minutos.");
        }
        landingInmuebleService.crearLead(id, request);
    }

    private String resolverIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @GetMapping("/{id}/fotos/{fotoId}")
    public ResponseEntity<byte[]> foto(@PathVariable UUID id, @PathVariable UUID fotoId) {
        byte[] bytes = landingInmuebleService.leerFoto(id, fotoId);
        MediaType tipoContenido = landingInmuebleService.tipoContenidoDeFoto(id, fotoId);

        return ResponseEntity.ok().contentType(tipoContenido).body(bytes);
    }

    @GetMapping("/{id}/videos/{videoId}")
    public ResponseEntity<ResourceRegion> video(@PathVariable UUID id, @PathVariable UUID videoId,
                                                 @RequestHeader HttpHeaders headers) throws IOException {
        Resource recurso = landingInmuebleService.obtenerRecursoVideo(id, videoId);
        MediaType tipoContenido = landingInmuebleService.tipoContenidoDeVideo(id, videoId);

        ResourceRegion region = resolverRegion(recurso, headers);

        return ResponseEntity.status(HttpStatus.PARTIAL_CONTENT)
                .contentType(tipoContenido)
                .body(region);
    }

    private ResourceRegion resolverRegion(Resource recurso, HttpHeaders headers) throws IOException {
        long tamanoTotal = recurso.contentLength();
        HttpRange rango = headers.getRange().stream().findFirst().orElse(null);

        if (rango != null) {
            long inicio = rango.getRangeStart(tamanoTotal);
            long fin = rango.getRangeEnd(tamanoTotal);
            long largo = Math.min(TAMANO_FRAGMENTO, fin - inicio + 1);
            return new ResourceRegion(recurso, inicio, largo);
        }

        long largo = Math.min(TAMANO_FRAGMENTO, tamanoTotal);
        return new ResourceRegion(recurso, 0, largo);
    }
}
