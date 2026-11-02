package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.AsignarCanalesRequest;
import com.felipeparodi.crmbackend.marketing.dto.CanalPublicacionVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.SolicitudVideoCanalService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-video/{solicitudVideoId}/canales")
public class SolicitudVideoCanalController {

    private final SolicitudVideoCanalService solicitudVideoCanalService;

    public SolicitudVideoCanalController(SolicitudVideoCanalService solicitudVideoCanalService) {
        this.solicitudVideoCanalService = solicitudVideoCanalService;
    }

    @PostMapping
    public List<CanalPublicacionVideoResponse> asignar(@PathVariable UUID solicitudVideoId,
                                                          @RequestBody AsignarCanalesRequest request) {
        return solicitudVideoCanalService.asignar(solicitudVideoId, request);
    }

    @GetMapping
    public List<CanalPublicacionVideoResponse> listar(@PathVariable UUID solicitudVideoId) {
        return solicitudVideoCanalService.listarPorSolicitud(solicitudVideoId);
    }
}
