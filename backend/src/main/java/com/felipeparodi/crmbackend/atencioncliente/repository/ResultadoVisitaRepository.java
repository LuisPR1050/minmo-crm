package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ResultadoVisitaRepository extends JpaRepository<ResultadoVisita, UUID> {

    Optional<ResultadoVisita> findByVisitaId(UUID visitaId);
}