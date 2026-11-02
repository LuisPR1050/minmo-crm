package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.AsignarDestinosRequest;
import com.felipeparodi.crmbackend.marketing.dto.DestinoVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.SolicitudVideoDestinoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-video/{solicitudVideoId}/destinos")
public class SolicitudVideoDestinoController {

    private final SolicitudVideoDestinoService solicitudVideoDestinoService;

    public SolicitudVideoDestinoController(SolicitudVideoDestinoService solicitudVideoDestinoService) {
        this.solicitudVideoDestinoService = solicitudVideoDestinoService;
    }

    @PostMapping
    public List<DestinoVideoResponse> asignar(@PathVariable UUID solicitudVideoId,
                                                @RequestBody AsignarDestinosRequest request) {
        return solicitudVideoDestinoService.asignar(solicitudVideoId, request);
    }

    @GetMapping
    public List<DestinoVideoResponse> listar(@PathVariable UUID solicitudVideoId) {
        return solicitudVideoDestinoService.listarPorSolicitud(solicitudVideoId);
    }
}
