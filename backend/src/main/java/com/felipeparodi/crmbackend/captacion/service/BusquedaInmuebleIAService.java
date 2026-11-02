package com.felipeparodi.crmbackend.captacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.captacion.dto.FiltrosBusquedaInmuebleIA;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;

@Service
public class BusquedaInmuebleIAService {

    private static final String MODELO = "claude-opus-5";

    private final AnthropicClient anthropicClient;
    private final InmuebleService inmuebleService;

    public BusquedaInmuebleIAService(AnthropicClient anthropicClient, InmuebleService inmuebleService) {
        this.anthropicClient = anthropicClient;
        this.inmuebleService = inmuebleService;
    }

    public List<InmuebleResponse> buscar(String consulta) {
        if (consulta == null || consulta.isBlank()) {
            return inmuebleService.listarTodos();
        }

        FiltrosBusquedaInmuebleIA filtros = interpretarConsulta(consulta);

        return inmuebleService.listarTodos().stream()
                .filter(inmueble -> cumpleFiltros(inmueble, filtros))
                .toList();
    }

    private FiltrosBusquedaInmuebleIA interpretarConsulta(String consulta) {
        String prompt = "Interpreta esta busqueda en lenguaje natural (puede venir de dictado por voz, con "
                + "errores de transcripcion) de un asesor inmobiliario en Colombia buscando inmuebles para un "
                + "cliente, y extrae los criterios de busqueda mencionados. No inventes criterios que no se "
                + "mencionen: deja los campos de texto como cadena vacia \"\" y los campos numericos en 0 cuando "
                + "el criterio correspondiente no se menciona en la busqueda.\n\nBusqueda: \"" + consulta + "\"";

        StructuredMessageCreateParams<FiltrosBusquedaInmuebleIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1024L)
                .outputConfig(FiltrosBusquedaInmuebleIA.class)
                .addUserMessage(prompt)
                .build();

        return anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException("La IA no pudo interpretar la busqueda"));
    }

    private boolean cumpleFiltros(InmuebleResponse inmueble, FiltrosBusquedaInmuebleIA filtros) {
        if (tieneValor(filtros.tipoPropiedad())
                && !filtros.tipoPropiedad().equalsIgnoreCase(inmueble.getTipoPropiedad())) {
            return false;
        }
        if (tieneValor(filtros.ciudad())
                && !contieneIgnoreCase(inmueble.getCiudad(), filtros.ciudad())) {
            return false;
        }
        if (tieneValor(filtros.zona())
                && !filtros.zona().equalsIgnoreCase(inmueble.getZona())) {
            return false;
        }
        if (tieneValor(filtros.entorno())
                && !filtros.entorno().equalsIgnoreCase(inmueble.getEntorno())) {
            return false;
        }
        if (filtros.puntosReferencia() != null && !filtros.puntosReferencia().isEmpty()
                && filtros.puntosReferencia().stream().noneMatch(punto -> coincideConPuntoReferencia(inmueble, punto))) {
            return false;
        }
        if (filtros.precioMinimo() > 0
                && (inmueble.getExpectativaPrecio() == null
                        || inmueble.getExpectativaPrecio().compareTo(BigDecimal.valueOf(filtros.precioMinimo())) < 0)) {
            return false;
        }
        if (filtros.precioMaximo() > 0
                && (inmueble.getExpectativaPrecio() == null
                        || inmueble.getExpectativaPrecio().compareTo(BigDecimal.valueOf(filtros.precioMaximo())) > 0)) {
            return false;
        }
        if (filtros.habitacionesMinimo() > 0
                && (inmueble.getNumeroHabitaciones() == null
                        || inmueble.getNumeroHabitaciones() < filtros.habitacionesMinimo())) {
            return false;
        }
        if (filtros.banosMinimo() > 0
                && (inmueble.getNumeroBanos() == null || inmueble.getNumeroBanos() < filtros.banosMinimo())) {
            return false;
        }
        if (filtros.estrato() > 0
                && (inmueble.getEstratoSocioeconomico() == null
                        || inmueble.getEstratoSocioeconomico() != filtros.estrato())) {
            return false;
        }
        if (filtros.areaMinima() > 0
                && (inmueble.getAreaTotal() == null
                        || inmueble.getAreaTotal().compareTo(BigDecimal.valueOf(filtros.areaMinima())) < 0)) {
            return false;
        }
        if (tieneValor(filtros.estadoJuridico())
                && !filtros.estadoJuridico().equalsIgnoreCase(inmueble.getEstadoJuridico())) {
            return false;
        }
        if (tieneValor(filtros.estadoCaptacion())
                && !filtros.estadoCaptacion().equalsIgnoreCase(inmueble.getEstadoCaptacion())) {
            return false;
        }

        return true;
    }

    private boolean tieneValor(String valor) {
        return valor != null && !valor.isBlank();
    }

    private boolean coincideConPuntoReferencia(InmuebleResponse inmueble, String punto) {
        return contieneIgnoreCase(inmueble.getZona(), punto)
                || contieneIgnoreCase(inmueble.getDireccionExacta(), punto)
                || contieneIgnoreCase(inmueble.getPuntosClavesCercanos(), punto)
                || (inmueble.getPuntosReferencia() != null
                        && inmueble.getPuntosReferencia().stream().anyMatch(pr -> contieneIgnoreCase(pr, punto)));
    }

    private boolean contieneIgnoreCase(String valor, String buscado) {
        return valor != null && valor.toLowerCase(Locale.ROOT).contains(buscado.toLowerCase(Locale.ROOT));
    }
}
