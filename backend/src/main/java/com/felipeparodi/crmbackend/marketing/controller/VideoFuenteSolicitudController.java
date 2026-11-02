package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.VideoFuenteSolicitudRequest;
import com.felipeparodi.crmbackend.marketing.dto.VideoFuenteSolicitudResponse;
import com.felipeparodi.crmbackend.marketing.service.VideoFuenteSolicitudService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-video/{solicitudVideoId}/videos-fuente")
public class VideoFuenteSolicitudController {

    private final VideoFuenteSolicitudService videoFuenteSolicitudService;

    public VideoFuenteSolicitudController(VideoFuenteSolicitudService videoFuenteSolicitudService) {
        this.videoFuenteSolicitudService = videoFuenteSolicitudService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoFuenteSolicitudResponse agregar(@PathVariable UUID solicitudVideoId,
                                                  @RequestBody VideoFuenteSolicitudRequest request) {
        return videoFuenteSolicitudService.agregar(solicitudVideoId, request);
    }

    @GetMapping
    public List<VideoFuenteSolicitudResponse> listar(@PathVariable UUID solicitudVideoId) {
        return videoFuenteSolicitudService.listarPorSolicitud(solicitudVideoId);
    }
}
