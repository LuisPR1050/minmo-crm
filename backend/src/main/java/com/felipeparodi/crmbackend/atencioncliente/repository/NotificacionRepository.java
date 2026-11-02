package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NotificacionRepository extends JpaRepository<Notificacion, UUID> {

    List<Notificacion> findByLeidaFalse();
}