package com.felipeparodi.crmbackend.marketing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "copia_video_canal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CopiaVideoCanal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "solicitud_video_id", nullable = false)
    private SolicitudVideoMarketing solicitudVideo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "canal_publicacion_video_id", nullable = false)
    private CanalPublicacionVideo canalPublicacionVideo;

    private String texto;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
