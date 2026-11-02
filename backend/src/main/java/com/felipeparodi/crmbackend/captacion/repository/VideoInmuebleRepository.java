package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.VideoInmueble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoInmuebleRepository extends JpaRepository<VideoInmueble, UUID> {

    List<VideoInmueble> findByInmuebleId(UUID inmuebleId);
}
