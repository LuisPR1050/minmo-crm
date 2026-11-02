package com.felipeparodi.crmbackend.captacion.service;

import com.anthropic.client.AnthropicClient;
import com.anthropic.models.messages.MessageCreateParams;
import com.anthropic.models.messages.StructuredMessageCreateParams;
import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.EstadoPipelineCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.RequerimientoCliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.RequerimientoClienteRepository;
import com.felipeparodi.crmbackend.captacion.dto.ClienteSugeridoResponse;
import com.felipeparodi.crmbackend.captacion.dto.ClientesSugeridosIA;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ClienteSugeridoIAService {

    private static final String MODELO = "claude-opus-5";
    private static final Set<EstadoPipelineCliente> ESTADOS_FINALIZADOS =
            Set.of(EstadoPipelineCliente.NEGOCIO_CERRADO_GANADO, EstadoPipelineCliente.NEGOCIO_PERDIDO);

    private final AnthropicClient anthropicClient;
    private final InmuebleService inmuebleService;
    private final RequerimientoClienteRepository requerimientoClienteRepository;

    public ClienteSugeridoIAService(AnthropicClient anthropicClient,
                                     InmuebleService inmuebleService,
                                     RequerimientoClienteRepository requerimientoClienteRepository) {
        this.anthropicClient = anthropicClient;
        this.inmuebleService = inmuebleService;
        this.requerimientoClienteRepository = requerimientoClienteRepository;
    }

    public List<ClienteSugeridoResponse> sugerir(UUID inmuebleId) {
        InmuebleResponse inmueble = inmuebleService.buscarPorId(inmuebleId);

        List<RequerimientoCliente> candidatos = requerimientoClienteRepository.findAll().stream()
                .filter(r -> r.getCliente().getEstadoPipeline() == null
                        || !ESTADOS_FINALIZADOS.contains(r.getCliente().getEstadoPipeline()))
                .toList();

        if (candidatos.isEmpty()) {
            throw new IllegalArgumentException(
                    "No hay clientes con requerimientos pendientes para comparar con esta propiedad");
        }

        Map<String, RequerimientoCliente> candidatosPorReferencia = new LinkedHashMap<>();
        int indice = 1;
        for (RequerimientoCliente r : candidatos) {
            candidatosPorReferencia.put("REF-" + indice, r);
            indice++;
        }

        String prompt = construirPrompt(inmueble, candidatosPorReferencia);

        StructuredMessageCreateParams<ClientesSugeridosIA> params = MessageCreateParams.builder()
                .model(MODELO)
                .maxTokens(1536L)
                .outputConfig(ClientesSugeridosIA.class)
                .addUserMessage(prompt)
                .build();

        ClientesSugeridosIA resultado = anthropicClient.messages().create(params).content().stream()
                .flatMap(block -> block.text().stream())
                .findFirst()
                .map(typed -> typed.text())
                .orElseThrow(() -> new IllegalStateException(
                        "La IA no genero sugerencias de clientes para el inmueble " + inmuebleId));

        return resultado.sugerencias().stream()
                .filter(s -> candidatosPorReferencia.containsKey(s.referencia()))
                .map(s -> {
                    RequerimientoCliente r = candidatosPorReferencia.get(s.referencia());
                    Cliente cliente = r.getCliente();
                    return ClienteSugeridoResponse.builder()
                            .clienteId(cliente.getId())
                            .clienteNombre(cliente.getNombre())
                            .requerimientoId(r.getId())
                            .motivo(s.motivo())
                            .build();
                })
                .toList();
    }

    private String construirPrompt(InmuebleResponse inmueble, Map<String, RequerimientoCliente> candidatos) {
        StringBuilder p = new StringBuilder();
        p.append("Eres un asesor inmobiliario experto en Valledupar, Colombia. Se acaba de registrar una propiedad ");
        p.append("nueva en el inventario. Revisa los requerimientos de los clientes pendientes (que aun no cerraron ");
        p.append("ni perdieron el negocio) y sugiere hasta 5 clientes con mayor probabilidad de interes en esta ");
        p.append("propiedad, usando la referencia exacta de cada uno tal como aparece en la lista. No inventes ");
        p.append("clientes que no esten en esta lista.\n\n");

        p.append("PROPIEDAD NUEVA\n");
        p.append("Tipo: ").append(inmueble.getTipoPropiedad()).append("\n");
        p.append("Ciudad: ").append(inmueble.getCiudad()).append("\n");
        if (inmueble.getZona() != null) p.append("Zona: ").append(inmueble.getZona()).append("\n");
        if (inmueble.getEntorno() != null) p.append("Entorno: ").append(inmueble.getEntorno()).append("\n");
        if (inmueble.getAreaTotal() != null) p.append("Area total: ").append(inmueble.getAreaTotal()).append(" m2\n");
        if (inmueble.getNumeroHabitaciones() != null) p.append("Habitaciones: ").append(inmueble.getNumeroHabitaciones()).append("\n");
        if (inmueble.getNumeroBanos() != null) p.append("Banos: ").append(inmueble.getNumeroBanos()).append("\n");
        if (inmueble.getExpectativaPrecio() != null) p.append("Precio: ").append(inmueble.getExpectativaPrecio()).append(" COP\n");

        p.append("\nCLIENTES CON REQUERIMIENTOS PENDIENTES (").append(candidatos.size()).append(")\n");
        candidatos.forEach((referencia, r) -> {
            p.append("- ").append(referencia).append(": ").append(r.getCliente().getNombre());
            if (r.getTipoPropiedad() != null) p.append(", busca ").append(r.getTipoPropiedad());
            if (r.getCiudad() != null) p.append(" en ").append(r.getCiudad());
            if (r.getZona() != null) p.append(" zona ").append(r.getZona());
            if (r.getPresupuestoMin() != null || r.getPresupuestoMax() != null) {
                p.append(", presupuesto ").append(r.getPresupuestoMin()).append("-").append(r.getPresupuestoMax()).append(" COP");
            }
            if (r.getNumeroHabitaciones() != null) p.append(", ").append(r.getNumeroHabitaciones()).append(" hab");
            if (r.getTiempoParaComprar() != null) p.append(", tiempo para comprar: ").append(r.getTiempoParaComprar());
            p.append(", estado del cliente: ").append(r.getCliente().getEstadoPipeline()).append("\n");
        });

        return p.toString();
    }
}
