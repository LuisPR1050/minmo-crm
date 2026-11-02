package com.felipeparodi.crmbackend.atencioncliente.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.atencioncliente.dto.InmuebleRecomendadoResponse;
import com.felipeparodi.crmbackend.atencioncliente.dto.RecomendacionInmuebleIA;
import com.felipeparodi.crmbackend.atencioncliente.entity.RequerimientoCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.RequerimientoClienteRepository;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import com.felipeparodi.crmbackend.captacion.service.InmuebleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class RecomendacionInmuebleIAService {

    private static final String MODELO = "claude-opus-5";
    private static final Set<String> ESTADOS_NO_DISPONIBLES = Set.of("VENDIDO", "NEGOCIO_PERDIDO", "DESCARTADO");

    private final AnthropicClient anthropicClient;
    private final RequerimientoClienteRepository requerimientoClienteRepository;
    private final InmuebleService inmuebleService;

    public RecomendacionInmuebleIAService(AnthropicClient anthropicClient,
                                           RequerimientoClienteRepository requerimientoClienteRepository,
                                           InmuebleService inmuebleService) {
        this.anthropicClient = anthropicClient;
        this.requerimientoClienteRepository = requerimientoClienteRepository;
        this.inmuebleService = inmuebleService;
    }

    public List<InmuebleRecomendadoResponse> recomendar(UUID requerimientoId) {
        RequerimientoCliente requerimiento = requerimientoClienteRepository.findById(requerimientoId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Requerimiento no encontrado con id: " + requerimientoId));

        List<InmuebleResponse> candidatos = inmuebleService.listarTodos().stream()
                .filter(i -> !ESTADOS_NO_DISPONIBLES.contains(i.getEstadoCaptacion()))
                .filter(i -> requerimiento.getCiudad() == null
                        || requerimiento.getCiudad().equalsIgnoreCase(i.getCiudad()))
                .toList();

        if (candidatos.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay inmuebles disponibles en el inventario para recomendar (ninguno coincide con la ciudad buscada o el inventario esta vacio)");
        }

        Map<String, InmuebleResponse> candidatosPorCodigo = new LinkedHashMap<>();
        candidatos.forEach(i -> candidatosPorCodigo.put(i.getCodigo(), i));

        String prompt = construirPrompt(requerimiento, candidatos);

        StructuredMessageCreateParams<RecomendacionInmuebleIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1536L)
                .outputConfig(RecomendacionInmuebleIA.class)
                .addUserMessage(prompt)
                .build();

        RecomendacionInmuebleIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero recomendaciones para el requerimiento " + requerimientoId));

        return resultado.recomendaciones().stream()
                .filter(r -> candidatosPorCodigo.containsKey(r.codigoInmueble()))
                .map(r -> InmuebleRecomendadoResponse.builder()
                        .inmueble(candidatosPorCodigo.get(r.codigoInmueble()))
                        .motivo(r.motivo())
                        .build())
                .toList();
    }

    private String construirPrompt(RequerimientoCliente requerimiento, List<InmuebleResponse> candidatos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un asesor inmobiliario experto en Valledupar, Colombia. Recomienda hasta 5 inmuebles del ");
        p.append("inventario disponible que mejor encajen con lo que busca este cliente. Prioriza los que cumplan ");
        p.append("presupuesto, tipo de propiedad, zona y numero de habitaciones/banos, pero usa tu criterio si no ");
        p.append("hay una coincidencia perfecta. No inventes inmuebles que no esten en la lista de candidatos.\n\n");

        p.append("LO QUE BUSCA EL CLIENTE\n");
        if (requerimiento.getTipoPropiedad() != null) p.append("Tipo de propiedad: ").append(requerimiento.getTipoPropiedad()).append("\n");
        if (requerimiento.getCiudad() != null) p.append("Ciudad: ").append(requerimiento.getCiudad()).append("\n");
        if (requerimiento.getZona() != null) p.append("Zona: ").append(requerimiento.getZona()).append("\n");
        if (requerimiento.getEntorno() != null) p.append("Entorno: ").append(requerimiento.getEntorno()).append("\n");
        if (requerimiento.getSitiosClavesCercanos() != null) p.append("Sitios cercanos deseados: ").append(requerimiento.getSitiosClavesCercanos()).append("\n");
        if (requerimiento.getPresupuestoMin() != null) p.append("Presupuesto minimo: ").append(requerimiento.getPresupuestoMin()).append(" COP\n");
        if (requerimiento.getPresupuestoMax() != null) p.append("Presupuesto maximo: ").append(requerimiento.getPresupuestoMax()).append(" COP\n");
        if (requerimiento.getFormaPago() != null) p.append("Forma de pago: ").append(requerimiento.getFormaPago()).append("\n");
        if (requerimiento.getNumeroHabitaciones() != null) p.append("Habitaciones deseadas: ").append(requerimiento.getNumeroHabitaciones()).append("\n");
        if (requerimiento.getNumeroBanos() != null) p.append("Banos deseados: ").append(requerimiento.getNumeroBanos()).append("\n");
        if (requerimiento.getObservaciones() != null) p.append("Observaciones: ").append(requerimiento.getObservaciones()).append("\n");

        p.append("\nINVENTARIO DISPONIBLE (").append(candidatos.size()).append(")\n");
        for (InmuebleResponse c : candidatos) {
            p.append("- ").append(c.getCodigo()).append(": ").append(c.getTipoPropiedad());
            if (c.getZona() != null) p.append(", zona ").append(c.getZona());
            if (c.getAreaTotal() != null) p.append(", ").append(c.getAreaTotal()).append(" m2");
            if (c.getNumeroHabitaciones() != null) p.append(", ").append(c.getNumeroHabitaciones()).append(" hab");
            if (c.getNumeroBanos() != null) p.append(", ").append(c.getNumeroBanos()).append(" banos");
            if (c.getExpectativaPrecio() != null) p.append(", precio ").append(c.getExpectativaPrecio()).append(" COP");
            p.append(", estado: ").append(c.getEstadoCaptacion()).append("\n");
        }

        return p.toString();
    }
}
