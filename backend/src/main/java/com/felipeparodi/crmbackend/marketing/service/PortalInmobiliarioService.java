package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.PortalInmobiliarioResponse;
import com.felipeparodi.crmbackend.marketing.repository.PortalInmobiliarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortalInmobiliarioService {

    private final PortalInmobiliarioRepository portalInmobiliarioRepository;

    public PortalInmobiliarioService(PortalInmobiliarioRepository portalInmobiliarioRepository) {
        this.portalInmobiliarioRepository = portalInmobiliarioRepository;
    }

    public List<PortalInmobiliarioResponse> listarTodos() {
        return portalInmobiliarioRepository.findAll()
                .stream()
                .map(p -> PortalInmobiliarioResponse.builder()
                        .id(p.getId())
                        .nombre(p.getNombre())
                        .build())
                .toList();
    }
}
