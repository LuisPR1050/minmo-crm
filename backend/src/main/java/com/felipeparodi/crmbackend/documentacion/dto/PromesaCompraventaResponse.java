package com.felipeparodi.crmbackend.documentacion.dto;

import com.felipeparodi.crmbackend.negociacion.dto.ParteNegociacionResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class PromesaCompraventaResponse {

    private UUID id;
    private String numeroContrato;
    private UUID negociacionId;
    private String numeroNegocio;
    private UUID inmuebleId;
    private String inmuebleCodigo;
    private List<ParteNegociacionResponse> compradores;
    private List<ParteNegociacionResponse> vendedores;
    private LocalDate fechaElaboracion;
    private String ciudad;
    private String opcionFirma;
    private String estado;
    private String textoPromesa;
    private LocalDateTime fechaGeneracionTexto;
    private String pdfUrl;
    private LocalDateTime createdAt;
}
