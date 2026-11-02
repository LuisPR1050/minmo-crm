package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.ActualizarSolicitudVideoMarketingRequest;
import com.felipeparodi.crmbackend.marketing.dto.CambiarEstadoSolicitudVideoRequest;
import com.felipeparodi.crmbackend.marketing.dto.CopiaVideoCanalResponse;
import com.felipeparodi.crmbackend.marketing.dto.GenerarVideoRequest;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingRequest;
import com.felipeparodi.crmbackend.marketing.dto.SolicitudVideoMarketingResponse;
import com.felipeparodi.crmbackend.documentacion.dto.GenerarTextoIARequest;
import com.felipeparodi.crmbackend.marketing.service.CopyVideoMarketingIAService;
import com.felipeparodi.crmbackend.marketing.service.SolicitudVideoMarketingService;
import com.felipeparodi.crmbackend.marketing.service.VideoSlideshowService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-video")
public class SolicitudVideoMarketingController {

    private final SolicitudVideoMarketingService solicitudVideoMarketingService;
    private final CopyVideoMarketingIAService copyVideoMarketingIAService;
    private final VideoSlideshowService videoSlideshowService;

    public SolicitudVideoMarketingController(SolicitudVideoMarketingService solicitudVideoMarketingService,
                                              CopyVideoMarketingIAService copyVideoMarketingIAService,
                                              VideoSlideshowService videoSlideshowService) {
        this.solicitudVideoMarketingService = solicitudVideoMarketingService;
        this.copyVideoMarketingIAService = copyVideoMarketingIAService;
        this.videoSlideshowService = videoSlideshowService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SolicitudVideoMarketingResponse crear(@RequestBody SolicitudVideoMarketingRequest request) {
        return solicitudVideoMarketingService.crear(request);
    }

    @GetMapping
    public List<SolicitudVideoMarketingResponse> listar(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return solicitudVideoMarketingService.listarPorInmueble(inmuebleId);
        }
        return solicitudVideoMarketingService.listarTodas();
    }

    @GetMapping("/{id}")
    public SolicitudVideoMarketingResponse buscarPorId(@PathVariable UUID id) {
        return solicitudVideoMarketingService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public SolicitudVideoMarketingResponse actualizar(@PathVariable UUID id,
                                                        @RequestBody ActualizarSolicitudVideoMarketingRequest request) {
        return solicitudVideoMarketingService.actualizar(id, request);
    }

    @PatchMapping("/{id}/estado")
    public SolicitudVideoMarketingResponse cambiarEstado(@PathVariable UUID id,
                                                           @RequestBody CambiarEstadoSolicitudVideoRequest request) {
        return solicitudVideoMarketingService.cambiarEstado(id, request);
    }

    @PostMapping("/{id}/logo")
    public SolicitudVideoMarketingResponse subirLogo(@PathVariable UUID id, @RequestParam("logo") MultipartFile logo) {
        return solicitudVideoMarketingService.subirLogo(id, logo);
    }

    @PostMapping("/{id}/musica")
    public SolicitudVideoMarketingResponse subirMusica(@PathVariable UUID id, @RequestParam("musica") MultipartFile musica) {
        return solicitudVideoMarketingService.subirMusica(id, musica);
    }

    @DeleteMapping("/{id}/musica")
    public SolicitudVideoMarketingResponse eliminarMusica(@PathVariable UUID id) {
        return solicitudVideoMarketingService.eliminarMusica(id);
    }

    @PostMapping("/{id}/generar-copy")
    public SolicitudVideoMarketingResponse generarCopy(@PathVariable UUID id,
                                                         @RequestBody(required = false) GenerarTextoIARequest request) {
        String instrucciones = request != null ? request.getInstruccionesAdicionales() : null;
        return copyVideoMarketingIAService.generarCopy(id, instrucciones);
    }

    @PostMapping("/{id}/generar-video")
    public SolicitudVideoMarketingResponse generarVideo(@PathVariable UUID id,
                                                          @RequestBody(required = false) GenerarVideoRequest request) {
        boolean incluirVozOff = request != null && request.isIncluirVozOff();
        return videoSlideshowService.generarVideo(id, incluirVozOff);
    }

    @GetMapping("/{id}/copias-por-canal")
    public List<CopiaVideoCanalResponse> listarCopiasPorCanal(@PathVariable UUID id) {
        return copyVideoMarketingIAService.listarCopiasPorCanal(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        solicitudVideoMarketingService.eliminar(id);
    }
}
