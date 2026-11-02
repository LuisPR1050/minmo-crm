package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.Caracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CaracteristicaRepository extends JpaRepository<Caracteristica, UUID> {

    List<Caracteristica> findByCategoria(String categoria);
}