package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.PortalInmobiliario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface PortalInmobiliarioRepository extends JpaRepository<PortalInmobiliario, UUID> {
}
