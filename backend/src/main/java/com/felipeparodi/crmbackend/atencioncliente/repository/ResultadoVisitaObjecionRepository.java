package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisitaObjecion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ResultadoVisitaObjecionRepository extends JpaRepository<ResultadoVisitaObjecion, UUID> {

    List<ResultadoVisitaObjecion> findByResultadoVisitaId(UUID resultadoVisitaId);
}