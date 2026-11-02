package com.felipeparodi.crmbackend.documentacion.entity;

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
@Table(name = "documento_promesa_compraventa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DocumentoPromesaCompraventa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promesa_compraventa_id", nullable = false)
    private PromesaCompraventa promesaCompraventa;

    @Enumerated(EnumType.STRING)
    private TipoDocumentoPromesaCompraventa tipo;

    private String url;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
