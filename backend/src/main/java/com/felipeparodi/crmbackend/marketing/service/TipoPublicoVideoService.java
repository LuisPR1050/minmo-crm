package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.TipoPublicoVideoResponse;
import com.felipeparodi.crmbackend.marketing.repository.TipoPublicoVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TipoPublicoVideoService {

    private final TipoPublicoVideoRepository tipoPublicoVideoRepository;

    public TipoPublicoVideoService(TipoPublicoVideoRepository tipoPublicoVideoRepository) {
        this.tipoPublicoVideoRepository = tipoPublicoVideoRepository;
    }

    public List<TipoPublicoVideoResponse> listarTodos() {
        return tipoPublicoVideoRepository.findAll()
                .stream()
                .map(t -> TipoPublicoVideoResponse.builder()
                        .id(t.getId())
                        .nombre(t.getNombre())
                        .build())
                .toList();
    }
}
