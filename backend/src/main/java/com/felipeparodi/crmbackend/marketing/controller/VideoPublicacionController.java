package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.VideoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.VideoPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.service.VideoPublicacionService;
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
@RequestMapping("/api/publicaciones-inmuebles/{publicacionInmuebleId}/videos")
public class VideoPublicacionController {

    private final VideoPublicacionService videoPublicacionService;

    public VideoPublicacionController(VideoPublicacionService videoPublicacionService) {
        this.videoPublicacionService = videoPublicacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoPublicacionResponse agregar(@PathVariable UUID publicacionInmuebleId,
                                              @RequestBody VideoPublicacionRequest request) {
        return videoPublicacionService.agregar(publicacionInmuebleId, request);
    }

    @GetMapping
    public List<VideoPublicacionResponse> listar(@PathVariable UUID publicacionInmuebleId) {
        return videoPublicacionService.listarPorPublicacion(publicacionInmuebleId);
    }
}
