package com.felipeparodi.crmbackend.captacion.publico.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class LandingInmuebleResponse {

    private UUID id;
    private String codigo;
    private String tipoPropiedad;
    private String ciudad;
    private String zona;
    private String entorno;
    private BigDecimal areaTotal;
    private BigDecimal areaConstruida;
    private BigDecimal areaPrivada;
    private Short numeroHabitaciones;
    private Short numeroBanos;
    private Short piso;
    private String niveles;
    private String vista;
    private Short estratoSocioeconomico;
    private BigDecimal precio;
    private String descripcionPublica;
  private String ganchoEmocional;
    private List<LandingFotoResponse> fotos;
    private List<LandingVideoResponse> videos;
    private List<String> caracteristicas;
}
