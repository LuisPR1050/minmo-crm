package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.DestinoVideoResponse;
import com.felipeparodi.crmbackend.marketing.repository.DestinoVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class DestinoVideoService {

    private final DestinoVideoRepository destinoVideoRepository;

    public DestinoVideoService(DestinoVideoRepository destinoVideoRepository) {
        this.destinoVideoRepository = destinoVideoRepository;
    }

    public List<DestinoVideoResponse> listarTodos() {
        return destinoVideoRepository.findAll()
                .stream()
                .map(d -> DestinoVideoResponse.builder()
                        .id(d.getId())
                        .nombre(d.getNombre())
                        .build())
                .toList();
    }
}
