package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.TipoPublicoVideoResponse;
import com.felipeparodi.crmbackend.marketing.service.TipoPublicoVideoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/tipos-publico-video")
public class TipoPublicoVideoController {

    private final TipoPublicoVideoService tipoPublicoVideoService;

    public TipoPublicoVideoController(TipoPublicoVideoService tipoPublicoVideoService) {
        this.tipoPublicoVideoService = tipoPublicoVideoService;
    }

    @GetMapping
    public List<TipoPublicoVideoResponse> listar() {
        return tipoPublicoVideoService.listarTodos();
    }
}
