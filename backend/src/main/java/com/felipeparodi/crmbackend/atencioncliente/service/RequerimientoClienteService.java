package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.RequerimientoClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.RequerimientoClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.RequerimientoCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.RequerimientoClienteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class RequerimientoClienteService {

    private final RequerimientoClienteRepository requerimientoClienteRepository;
    private final ClienteRepository clienteRepository;

    public RequerimientoClienteService(RequerimientoClienteRepository requerimientoClienteRepository,
                                        ClienteRepository clienteRepository) {
        this.requerimientoClienteRepository = requerimientoClienteRepository;
        this.clienteRepository = clienteRepository;
    }

    public RequerimientoClienteResponse crear(RequerimientoClienteRequest request) {
        Cliente cliente = clienteRepository.findById(request.getClienteId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Cliente no encontrado con id: " + request.getClienteId()));

        RequerimientoCliente requerimiento = RequerimientoCliente.builder()
                .cliente(cliente)
                .tipoPropiedad(request.getTipoPropiedad())
                .ciudad(request.getCiudad())
                .zona(request.getZona())
                .entorno(request.getEntorno())
                .sitiosClavesCercanos(request.getSitiosClavesCercanos())
                .presupuestoMin(request.getPresupuestoMin())
                .presupuestoMax(request.getPresupuestoMax())
                .formaPago(request.getFormaPago())
                .tiempoParaComprar(request.getTiempoParaComprar())
                .numeroHabitaciones(request.getNumeroHabitaciones())
                .numeroBanos(request.getNumeroBanos())
                .observaciones(request.getObservaciones())
                .build();

        RequerimientoCliente guardado = requerimientoClienteRepository.save(requerimiento);

        return mapearAResponse(guardado);
    }

    public List<RequerimientoClienteResponse> listarPorCliente(UUID clienteId) {
        return requerimientoClienteRepository.findByClienteId(clienteId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public RequerimientoClienteResponse buscarPorId(UUID id) {
        RequerimientoCliente requerimiento = requerimientoClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Requerimiento no encontrado con id: " + id));

        return mapearAResponse(requerimiento);
    }

    public void eliminar(UUID id) {
        RequerimientoCliente requerimiento = requerimientoClienteRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Requerimiento no encontrado con id: " + id));

        requerimientoClienteRepository.delete(requerimiento);
    }

    private RequerimientoClienteResponse mapearAResponse(RequerimientoCliente requerimiento) {
        return RequerimientoClienteResponse.builder()
                .id(requerimiento.getId())
                .clienteId(requerimiento.getCliente().getId())
                .tipoPropiedad(requerimiento.getTipoPropiedad())
                .ciudad(requerimiento.getCiudad())
                .zona(requerimiento.getZona())
                .entorno(requerimiento.getEntorno())
                .sitiosClavesCercanos(requerimiento.getSitiosClavesCercanos())
                .presupuestoMin(requerimiento.getPresupuestoMin())
                .presupuestoMax(requerimiento.getPresupuestoMax())
                .formaPago(requerimiento.getFormaPago())
                .tiempoParaComprar(requerimiento.getTiempoParaComprar())
                .numeroHabitaciones(requerimiento.getNumeroHabitaciones())
                .numeroBanos(requerimiento.getNumeroBanos())
                .observaciones(requerimiento.getObservaciones())
                .createdAt(requerimiento.getCreatedAt())
                .build();
    }
}