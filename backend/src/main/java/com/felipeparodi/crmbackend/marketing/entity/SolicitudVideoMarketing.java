package com.felipeparodi.crmbackend.marketing.entity;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
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
@Table(name = "solicitud_video_marketing")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SolicitudVideoMarketing {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    @Enumerated(EnumType.STRING)
    private ObjetivoVideoMarketing objetivoPublicacion;

    @Enumerated(EnumType.STRING)
    private EstiloContenidoVideo estiloContenido;

    private Double duracionMinimaMinutos;

    private Double duracionMaximaMinutos;

    @Enumerated(EnumType.STRING)
    private LlamadoAccionVideo llamadoAccion;

    private String logoUrl;

    private String musicaFondoUrl;

    @Enumerated(EnumType.STRING)
    private EstadoSolicitudVideo estado;

    private String videoGeneradoUrl;

    private String tituloGenerado;

    private String subtituloGenerado;

    private String descripcionGenerada;

    private String llamadoAccionTexto;

    private String hashtags;

    private LocalDateTime fechaGeneracionCopy;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoSolicitudVideo.BORRADOR;
        }
    }
}
