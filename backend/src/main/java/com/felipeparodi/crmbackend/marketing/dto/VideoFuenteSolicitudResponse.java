package com.felipeparodi.crmbackend.marketing.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class VideoFuenteSolicitudResponse {

    private UUID id;
    private String url;
    private String tipoOrigen;
    private LocalDateTime createdAt;
}
