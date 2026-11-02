package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.CambiarEstadoPipelineRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.EstadoPipelineCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.ConfirmacionEliminacionRequeridaException;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.RequerimientoClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class ClienteService {

    private final ClienteRepository clienteRepository;
    private final NegociacionRepository negociacionRepository;
    private final RequerimientoClienteRepository requerimientoClienteRepository;
    private final VisitaClienteRepository visitaClienteRepository;
    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository;

    public ClienteService(ClienteRepository clienteRepository,
                           NegociacionRepository negociacionRepository,
                           RequerimientoClienteRepository requerimientoClienteRepository,
                           VisitaClienteRepository visitaClienteRepository,
                           ResultadoVisitaRepository resultadoVisitaRepository,
                           ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository) {
        this.clienteRepository = clienteRepository;
        this.negociacionRepository = negociacionRepository;
        this.requerimientoClienteRepository = requerimientoClienteRepository;
        this.visitaClienteRepository = visitaClienteRepository;
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.resultadoVisitaObjecionRepository = resultadoVisitaObjecionRepository;
    }

    public ClienteResponse crear(ClienteRequest request) {
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .telefono(request.getTelefono())
                .whatsapp(request.getWhatsapp())
                .correoElectronico(request.getCorreoElectronico())
                .tipoContacto(request.getTipoContacto())
                .subtipoContacto(request.getSubtipoContacto())
                .numeroContacto(request.getNumeroContacto())
                .asesorId(request.getAsesorId())
                .build();

        Cliente guardado = clienteRepository.save(cliente);

        return mapearAResponse(guardado);
    }

    public List<ClienteResponse> listarTodos() {
        return clienteRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public ClienteResponse buscarPorId(UUID id) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        return mapearAResponse(cliente);
    }

    @Transactional
    public void eliminar(UUID id, boolean confirmar) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        if (negociacionRepository.existsByClienteId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el cliente porque tiene negociaciones asociadas. "
                            + "Cierra o elimina la negociacion primero.");
        }

        var requerimientos = requerimientoClienteRepository.findByClienteId(id);
        var visitas = visitaClienteRepository.findByClienteId(id);

        List<String> resumen = new ArrayList<>();
        if (!requerimientos.isEmpty()) {
            resumen.add(requerimientos.size() + " requerimiento(s) registrado(s)");
        }
        if (!visitas.isEmpty()) {
            resumen.add(visitas.size() + " visita(s) registrada(s) (con sus resultados y objeciones, si los hay)");
        }

        if (!confirmar && !resumen.isEmpty()) {
            throw new ConfirmacionEliminacionRequeridaException(resumen);
        }

        requerimientoClienteRepository.deleteAll(requerimientos);
        for (VisitaCliente visita : visitas) {
            ResultadoVisita resultado = resultadoVisitaRepository.findByVisitaId(visita.getId()).orElse(null);
            if (resultado != null) {
                resultadoVisitaObjecionRepository.deleteAll(
                        resultadoVisitaObjecionRepository.findByResultadoVisitaId(resultado.getId()));
                resultadoVisitaRepository.delete(resultado);
            }
        }
        visitaClienteRepository.deleteAll(visitas);

        clienteRepository.delete(cliente);
    }

    public ClienteResponse cambiarEstadoPipeline(UUID id, CambiarEstadoPipelineRequest request) {
        Cliente cliente = clienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Cliente no encontrado con id: " + id));

        cliente.setEstadoPipeline(EstadoPipelineCliente.valueOf(request.getNuevoEstado()));

        Cliente actualizado = clienteRepository.save(cliente);

        return mapearAResponse(actualizado);
    }

    private ClienteResponse mapearAResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .nombre(cliente.getNombre())
                .telefono(cliente.getTelefono())
                .whatsapp(cliente.getWhatsapp())
                .correoElectronico(cliente.getCorreoElectronico())
                .tipoContacto(cliente.getTipoContacto())
                .subtipoContacto(cliente.getSubtipoContacto())
                .numeroContacto(cliente.getNumeroContacto())
                .asesorId(cliente.getAsesorId())
                .estadoPipeline(cliente.getEstadoPipeline().name())
                .createdAt(cliente.getCreatedAt())
                .build();
    }
}