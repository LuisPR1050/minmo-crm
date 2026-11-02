package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.NotificacionResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class NotificacionService {

    private final NotificacionRepository notificacionRepository;

    public NotificacionService(NotificacionRepository notificacionRepository) {
        this.notificacionRepository = notificacionRepository;
    }

    public List<NotificacionResponse> listarNoLeidas() {
        return notificacionRepository.findByLeidaFalse()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public NotificacionResponse marcarComoLeida(UUID id) {
        Notificacion notificacion = notificacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Notificacion no encontrada con id: " + id));

        notificacion.setLeida(true);
        Notificacion actualizada = notificacionRepository.save(notificacion);

        return mapearAResponse(actualizada);
    }

    private NotificacionResponse mapearAResponse(Notificacion notificacion) {
        return NotificacionResponse.builder()
                .id(notificacion.getId())
                .tipo(notificacion.getTipo().name())
                .mensaje(notificacion.getMensaje())
                .referenciaId(notificacion.getReferenciaId())
                .leida(notificacion.isLeida())
                .createdAt(notificacion.getCreatedAt())
                .build();
    }
}