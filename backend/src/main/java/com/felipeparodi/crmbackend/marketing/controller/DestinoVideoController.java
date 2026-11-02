package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.DestinoVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.DestinoVideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/destinos-video")
public class DestinoVideoController {

    private final DestinoVideoService destinoVideoService;

    public DestinoVideoController(DestinoVideoService destinoVideoService) {
        this.destinoVideoService = destinoVideoService;
    }

    @GetMapping
    public List<DestinoVideoResponse> listar() {
        return destinoVideoService.listarTodos();
    }
}
