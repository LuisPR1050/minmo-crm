package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.AccesoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface AccesoEntregaRepository extends JpaRepository<AccesoEntrega, UUID> {
}
