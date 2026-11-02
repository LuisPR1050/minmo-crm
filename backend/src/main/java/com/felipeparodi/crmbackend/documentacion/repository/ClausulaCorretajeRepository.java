package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ClausulaCorretaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClausulaCorretajeRepository extends JpaRepository<ClausulaCorretaje, UUID> {
}
