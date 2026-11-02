package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AutorizacionPropietarioResponse;
import com.felipeparodi.crmbackend.documentacion.repository.AutorizacionPropietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AutorizacionPropietarioService {

    private final AutorizacionPropietarioRepository autorizacionPropietarioRepository;

    public AutorizacionPropietarioService(AutorizacionPropietarioRepository autorizacionPropietarioRepository) {
        this.autorizacionPropietarioRepository = autorizacionPropietarioRepository;
    }

    public List<AutorizacionPropietarioResponse> listarTodas() {
        return autorizacionPropietarioRepository.findAll()
                .stream()
                .map(a -> AutorizacionPropietarioResponse.builder()
                        .id(a.getId())
                        .nombre(a.getNombre())
                        .build())
                .toList();
    }
}
