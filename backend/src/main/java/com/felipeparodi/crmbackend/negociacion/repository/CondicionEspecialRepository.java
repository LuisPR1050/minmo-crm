package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.CondicionEspecial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CondicionEspecialRepository extends JpaRepository<CondicionEspecial, UUID> {
}