package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.PazYSalvoTipo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PazYSalvoTipoRepository extends JpaRepository<PazYSalvoTipo, UUID> {
}
