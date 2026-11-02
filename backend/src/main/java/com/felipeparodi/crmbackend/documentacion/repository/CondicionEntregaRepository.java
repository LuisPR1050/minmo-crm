package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.CondicionEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CondicionEntregaRepository extends JpaRepository<CondicionEntrega, UUID> {
}
