package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.VideoInmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.VideoInmuebleResponse;
import com.felipeparodi.crmbackend.captacion.service.VideoInmuebleService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
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
@RequestMapping("/api/inmuebles/{inmuebleId}/videos")
public class VideoInmuebleController {

    private final VideoInmuebleService videoInmuebleService;

    public VideoInmuebleController(VideoInmuebleService videoInmuebleService) {
        this.videoInmuebleService = videoInmuebleService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public VideoInmuebleResponse agregar(@PathVariable UUID inmuebleId, @RequestBody VideoInmuebleRequest request) {
        return videoInmuebleService.agregar(inmuebleId, request);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public VideoInmuebleResponse subir(@PathVariable UUID inmuebleId, @RequestParam("archivo") MultipartFile archivo) {
        return videoInmuebleService.subirArchivo(inmuebleId, archivo);
    }

    @GetMapping
    public List<VideoInmuebleResponse> listar(@PathVariable UUID inmuebleId) {
        return videoInmuebleService.listarPorInmueble(inmuebleId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID inmuebleId, @PathVariable UUID id) {
        videoInmuebleService.eliminar(id);
    }
}
