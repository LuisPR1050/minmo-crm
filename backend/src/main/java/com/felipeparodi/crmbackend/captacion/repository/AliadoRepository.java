package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.Aliado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AliadoRepository extends JpaRepository<Aliado, UUID> {
}
