package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.Objecion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ObjecionRepository extends JpaRepository<Objecion, UUID> {
}