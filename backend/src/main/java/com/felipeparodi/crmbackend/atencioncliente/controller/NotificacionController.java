package com.felipeparodi.crmbackend.atencioncliente.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.NotificacionResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.NotificacionService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/notificaciones")
public class NotificacionController {

    private final NotificacionService notificacionService;

    public NotificacionController(NotificacionService notificacionService) {
        this.notificacionService = notificacionService;
    }

    @GetMapping
    public List<NotificacionResponse> listarNoLeidas() {
        return notificacionService.listarNoLeidas();
    }

    @PatchMapping("/{id}/marcar-leida")
    public NotificacionResponse marcarComoLeida(@PathVariable UUID id) {
        return notificacionService.marcarComoLeida(id);
    }
}