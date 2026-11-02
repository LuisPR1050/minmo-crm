package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.AutorizacionPropietario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AutorizacionPropietarioRepository extends JpaRepository<AutorizacionPropietario, UUID> {
}
