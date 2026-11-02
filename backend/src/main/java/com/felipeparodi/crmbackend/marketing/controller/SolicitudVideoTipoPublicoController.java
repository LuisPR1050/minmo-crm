package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.AsignarTiposPublicoRequest;
import com.felipeparodi.crmbackend.marketing.dto.TipoPublicoVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.SolicitudVideoTipoPublicoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/solicitudes-video/{solicitudVideoId}/tipos-publico")
public class SolicitudVideoTipoPublicoController {

    private final SolicitudVideoTipoPublicoService solicitudVideoTipoPublicoService;

    public SolicitudVideoTipoPublicoController(SolicitudVideoTipoPublicoService solicitudVideoTipoPublicoService) {
        this.solicitudVideoTipoPublicoService = solicitudVideoTipoPublicoService;
    }

    @PostMapping
    public List<TipoPublicoVideoResponse> asignar(@PathVariable UUID solicitudVideoId,
                                                    @RequestBody AsignarTiposPublicoRequest request) {
        return solicitudVideoTipoPublicoService.asignar(solicitudVideoId, request);
    }

    @GetMapping
    public List<TipoPublicoVideoResponse> listar(@PathVariable UUID solicitudVideoId) {
        return solicitudVideoTipoPublicoService.listarPorSolicitud(solicitudVideoId);
    }
}
