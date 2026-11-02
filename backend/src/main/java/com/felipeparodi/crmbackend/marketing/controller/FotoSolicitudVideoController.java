package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.FotoSolicitudVideoRequest;
import com.felipeparodi.crmbackend.marketing.dto.FotoSolicitudVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.FotoSolicitudVideoService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/solicitudes-video/{solicitudVideoId}/fotos")
public class FotoSolicitudVideoController {

    private final FotoSolicitudVideoService fotoSolicitudVideoService;

    public FotoSolicitudVideoController(FotoSolicitudVideoService fotoSolicitudVideoService) {
        this.fotoSolicitudVideoService = fotoSolicitudVideoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FotoSolicitudVideoResponse agregar(@PathVariable UUID solicitudVideoId,
                                                @RequestBody FotoSolicitudVideoRequest request) {
        return fotoSolicitudVideoService.agregar(solicitudVideoId, request);
    }

    @GetMapping
    public List<FotoSolicitudVideoResponse> listar(@PathVariable UUID solicitudVideoId) {
        return fotoSolicitudVideoService.listarPorSolicitud(solicitudVideoId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public FotoSolicitudVideoResponse subir(@PathVariable UUID solicitudVideoId,
                                             @RequestParam("foto") MultipartFile foto) {
        return fotoSolicitudVideoService.subir(solicitudVideoId, foto);
    }
}
