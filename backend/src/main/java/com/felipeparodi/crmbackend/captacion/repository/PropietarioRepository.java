package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PropietarioRepository extends JpaRepository<Propietario, UUID> {
}