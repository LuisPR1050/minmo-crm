package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.marketing.dto.CanalPublicacionVideoResponse;
import com.felipeparodi.crmbackend.marketing.repository.CanalPublicacionVideoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CanalPublicacionVideoService {

    private final CanalPublicacionVideoRepository canalPublicacionVideoRepository;

    public CanalPublicacionVideoService(CanalPublicacionVideoRepository canalPublicacionVideoRepository) {
        this.canalPublicacionVideoRepository = canalPublicacionVideoRepository;
    }

    public List<CanalPublicacionVideoResponse> listarTodos() {
        return canalPublicacionVideoRepository.findAll()
                .stream()
                .map(c -> CanalPublicacionVideoResponse.builder()
                        .id(c.getId())
                        .nombre(c.getNombre())
                        .build())
                .toList();
    }
}
