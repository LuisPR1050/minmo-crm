package com.felipeparodi.crmbackend.documentacion.entity;

import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "promesa_compraventa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromesaCompraventa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroContrato;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false, unique = true)
    private Negociacion negociacion;

    private LocalDate fechaElaboracion;

    private String ciudad;

    @Enumerated(EnumType.STRING)
    private OpcionFirma opcionFirma;

    @Enumerated(EnumType.STRING)
    private EstadoDocumento estado;

    private boolean alertaFirmaEnviada;

    private String textoPromesa;

    private LocalDateTime fechaGeneracionTexto;

    @Column(name = "pdf_url")
    private String pdfUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoDocumento.BORRADOR;
        }
    }
}
