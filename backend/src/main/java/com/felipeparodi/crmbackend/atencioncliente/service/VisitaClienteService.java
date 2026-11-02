package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.CambiarEstadoVisitaClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ConfirmarVisitaRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.VisitaClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.VisitaClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.EstadoVisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.util.GeneradorCodigoVisitaCliente;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class VisitaClienteService {

    private final VisitaClienteRepository visitaClienteRepository;
    private final ClienteRepository clienteRepository;
    private final InmuebleRepository inmuebleRepository;
    private final GeneradorCodigoVisitaCliente generadorCodigoVisitaCliente;
    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final VisitaSmsNotificador visitaSmsNotificador;

    public VisitaClienteService(VisitaClienteRepository visitaClienteRepository,
                                 ClienteRepository clienteRepository,
                                 InmuebleRepository inmuebleRepository,
                                 GeneradorCodigoVisitaCliente generadorCodigoVisitaCliente,
                                 ResultadoVisitaRepository resultadoVisitaRepository,
                                 VisitaSmsNotificador visitaSmsNotificador) {
        this.visitaClienteRepository = visitaClienteRepository;
        this.clienteRepository = clienteRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.generadorCodigoVisitaCliente = generadorCodigoVisitaCliente;
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.visitaSmsNotificador = visitaSmsNotificador;
    }

    public VisitaClienteResponse crear(VisitaClienteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id: " + request.getClienteId()));

        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        String codigo = generadorCodigoVisitaCliente.generar();

        VisitaCliente visita = VisitaCliente.builder()
                .cliente(cliente)
                .inmueble(inmueble)
                .codigoVisita(codigo)
                .tipoVisita(TipoVisita.valueOf(request.getTipoVisita()))
                .fechaVisita(request.getFechaVisita())
                .horaInicio(request.getHoraInicio())
                .horaFin(request.getHoraFin())
                .recomendaciones(request.getRecomendaciones())
                .build();

        VisitaCliente guardada = visitaClienteRepository.save(visita);

        visitaSmsNotificador.notificar(guardada);

        return mapearAResponse(guardada);
    }

    public List<VisitaClienteResponse> listarPorCliente(UUID clienteId) {
        return visitaClienteRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public List<VisitaClienteResponse> listarTodos() {
        return visitaClienteRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public VisitaClienteResponse buscarPorId(UUID id) {
        VisitaCliente visita = visitaClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        return mapearAResponse(visita);
    }

    public VisitaClienteResponse confirmar(UUID id, ConfirmarVisitaRequest request) {
        VisitaCliente visita = visitaClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        visita.setConfirmadaPorCliente(request.isConfirmadaPorCliente());
        visita.setConfirmadaPorPropietario(request.isConfirmadaPorPropietario());

        VisitaCliente actualizada = visitaClienteRepository.save(visita);

        return mapearAResponse(actualizada);
    }

    public VisitaClienteResponse cambiarEstado(UUID id, CambiarEstadoVisitaClienteRequest request) {
        VisitaCliente visita = visitaClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        visita.setEstado(EstadoVisitaCliente.valueOf(request.getNuevoEstado()));

        VisitaCliente actualizada = visitaClienteRepository.save(visita);

        return mapearAResponse(actualizada);
    }

    public void eliminar(UUID id) {
        VisitaCliente visita = visitaClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Visita no encontrada con id: " + id));

        resultadoVisitaRepository.findByVisitaId(id).ifPresent(resultadoVisitaRepository::delete);
        visitaClienteRepository.delete(visita);
    }

    private VisitaClienteResponse mapearAResponse(VisitaCliente visita) {
        return VisitaClienteResponse.builder()
                .id(visita.getId())
                .clienteId(visita.getCliente().getId())
                .clienteNombre(visita.getCliente().getNombre())
                .inmuebleId(visita.getInmueble().getId())
                .inmuebleCodigo(visita.getInmueble().getCodigo())
                .codigoVisita(visita.getCodigoVisita())
                .estado(visita.getEstado().name())
                .tipoVisita(visita.getTipoVisita().name())
                .fechaVisita(visita.getFechaVisita())
                .horaInicio(visita.getHoraInicio())
                .horaFin(visita.getHoraFin())
                .confirmadaPorCliente(visita.isConfirmadaPorCliente())
                .confirmadaPorPropietario(visita.isConfirmadaPorPropietario())
                .recomendaciones(visita.getRecomendaciones())
                .createdAt(visita.getCreatedAt())
                .build();
    }
}