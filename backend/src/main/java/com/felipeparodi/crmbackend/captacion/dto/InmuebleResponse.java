package com.felipeparodi.crmbackend.captacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class InmuebleResponse {

    private UUID id;
    private UUID propietarioId;
    private String propietarioNombre;
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
    private String estadoJuridico;
    private String estadoCaptacion;
    private List<String> puntosReferencia;
    private String descripcionPublica;
    private String ganchoEmocional;
    private boolean landingActiva;
    private boolean landingMostrarPrecio;
    private LocalDateTime createdAt;
}