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
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "inmueble")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Inmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    private String codigo;

    private String tipoPropiedad;

    private String ciudad;

    private String direccionExacta;

    private String zona;

    private String entorno;

    private BigDecimal areaTotal;

    private BigDecimal areaConstruida;

    private BigDecimal areaPrivada;

    private Short numeroHabitaciones;

    private Short numeroBanos;

    private String tipoCocina;

    private String estadoFisico;

    private String matriculaInmobiliaria;

    private String referenciaCatastral;

    private BigDecimal expectativaPrecio;

    private BigDecimal precioMinimoAceptado;

    private BigDecimal precioSugerido;

    private BigDecimal valorAdministracion;

    private BigDecimal valorArriendo;

    private BigDecimal valorImpuestos;

    private BigDecimal valorAdeudado;

    private boolean escuchaOfertas;

    private Short estratoSocioeconomico;

    private String usoSuelo;

    private BigDecimal latitud;

    private BigDecimal longitud;

    private String puntosClavesCercanos;

    private String situada;

    private String niveles;

    private Short piso;

    private String vista;

    private String tipoAcceso;

    private BigDecimal medidaFrente;

    private BigDecimal medidaFondo;

    private String otrasCaracteristicasLote;

    private String quienOcupaActualmente;

    @Enumerated(EnumType.STRING)
    private EstadoJuridico estadoJuridico;

    @Enumerated(EnumType.STRING)
    private EstadoCaptacion estadoCaptacion;

    private String descripcionPublica;

    @Column(name = "gancho_emocional")
    private String ganchoEmocional;

    @Builder.Default
    private boolean landingActiva = true;

    @Builder.Default
    private boolean landingMostrarPrecio = true;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "inmueble_punto_referencia",
            joinColumns = @JoinColumn(name = "inmueble_id"),
            inverseJoinColumns = @JoinColumn(name = "punto_referencia_id")
    )
    @Builder.Default
    private Set<PuntoReferencia> puntosReferencia = new HashSet<>();

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}