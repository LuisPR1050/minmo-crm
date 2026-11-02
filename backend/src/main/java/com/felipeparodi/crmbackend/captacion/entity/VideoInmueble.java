package com.felipeparodi.crmbackend.captacion.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "video_inmueble")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VideoInmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    private String url;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private OrigenVideo origen = OrigenVideo.LINK_EXTERNO;

    @Column(name = "tipo_contenido")
    private String tipoContenido;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.origen == null) {
            this.origen = OrigenVideo.LINK_EXTERNO;
        }
    }
}
