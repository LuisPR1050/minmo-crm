package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.CanalPublicacionVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.CanalPublicacionVideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/canales-publicacion-video")
public class CanalPublicacionVideoController {

    private final CanalPublicacionVideoService canalPublicacionVideoService;

    public CanalPublicacionVideoController(CanalPublicacionVideoService canalPublicacionVideoService) {
        this.canalPublicacionVideoService = canalPublicacionVideoService;
    }

    @GetMapping
    public List<CanalPublicacionVideoResponse> listar() {
        return canalPublicacionVideoService.listarTodos();
    }
}
