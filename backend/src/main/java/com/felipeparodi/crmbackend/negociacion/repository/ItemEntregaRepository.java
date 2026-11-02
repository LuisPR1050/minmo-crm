package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.ItemEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ItemEntregaRepository extends JpaRepository<ItemEntrega, UUID> {
}