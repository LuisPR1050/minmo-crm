package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.CondicionCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.CondicionCaptacionResponse;
import com.felipeparodi.crmbackend.captacion.entity.Aliado;
import com.felipeparodi.crmbackend.captacion.entity.CondicionCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.AliadoRepository;
import com.felipeparodi.crmbackend.captacion.repository.CondicionCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class CondicionCaptacionService {

    private final CondicionCaptacionRepository condicionCaptacionRepository;
    private final InmuebleRepository inmuebleRepository;
    private final AliadoRepository aliadoRepository;
    private final PropietarioRepository propietarioRepository;

    public CondicionCaptacionService(CondicionCaptacionRepository condicionCaptacionRepository,
                                      InmuebleRepository inmuebleRepository,
                                      AliadoRepository aliadoRepository,
                                      PropietarioRepository propietarioRepository) {
        this.condicionCaptacionRepository = condicionCaptacionRepository;
        this.inmuebleRepository = inmuebleRepository;
        this.aliadoRepository = aliadoRepository;
        this.propietarioRepository = propietarioRepository;
    }

    public CondicionCaptacionResponse crear(CondicionCaptacionRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(request.getInmuebleId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Inmueble no encontrado con id: " + request.getInmuebleId()));

        if ("PROPIETARIO_DIRECTO".equals(request.getOrigenPropiedad()) && request.getPropietarioId() != null
                && !request.getPropietarioId().equals(inmueble.getPropietario().getId())) {
            Propietario propietario = propietarioRepository.findById(request.getPropietarioId())
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Propietario no encontrado con id: " + request.getPropietarioId()));
            inmueble.setPropietario(propietario);
            inmuebleRepository.save(inmueble);
        }

        CondicionCaptacion condicion = condicionCaptacionRepository.findByInmuebleId(request.getInmuebleId())
                .orElseGet(() -> CondicionCaptacion.builder().inmueble(inmueble).build());

        condicion.setAsesorId(request.getAsesorId());
        condicion.setOrigenPropiedad(request.getOrigenPropiedad());
        condicion.setAliadoNombre(request.getAliadoNombre());
        condicion.setAliadoTelefono(request.getAliadoTelefono());
        if (request.getAliadoId() != null) {
            Aliado aliado = aliadoRepository.findById(request.getAliadoId())
                    .orElseThrow(() -> new RecursoNoEncontradoException("Aliado no encontrado con id: " + request.getAliadoId()));
            condicion.setAliado(aliado);
        } else {
            condicion.setAliado(null);
        }
        condicion.setWhatsappAsesor(request.getWhatsappAsesor());
        condicion.setTipoComision(request.getTipoComision());
        condicion.setValorComision(request.getValorComision());
        condicion.setExclusividad(request.isExclusividad());

        CondicionCaptacion guardada = condicionCaptacionRepository.save(condicion);

        return mapearAResponse(guardada);
    }

    public List<CondicionCaptacionResponse> listarTodos() {
        return condicionCaptacionRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public CondicionCaptacionResponse buscarPorId(UUID id) {
        CondicionCaptacion condicion = condicionCaptacionRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Condicion de captacion no encontrada con id: " + id));

        return mapearAResponse(condicion);
    }

    public CondicionCaptacionResponse buscarPorInmueble(UUID inmuebleId) {
        CondicionCaptacion condicion = condicionCaptacionRepository.findByInmuebleId(inmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existen condiciones de captacion para el inmueble: " + inmuebleId));

        return mapearAResponse(condicion);
    }

    private CondicionCaptacionResponse mapearAResponse(CondicionCaptacion condicion) {
        Aliado aliado = condicion.getAliado();
        return CondicionCaptacionResponse.builder()
                .id(condicion.getId())
                .inmuebleId(condicion.getInmueble().getId())
                .inmuebleCodigo(condicion.getInmueble().getCodigo())
                .asesorId(condicion.getAsesorId())
                .origenPropiedad(condicion.getOrigenPropiedad())
                .aliadoId(aliado != null ? aliado.getId() : null)
                .aliadoNombre(aliado != null ? aliado.getNombre() : condicion.getAliadoNombre())
                .aliadoTelefono(aliado != null ? aliado.getTelefono() : condicion.getAliadoTelefono())
                .whatsappAsesor(condicion.getWhatsappAsesor())
                .tipoComision(condicion.getTipoComision())
                .valorComision(condicion.getValorComision())
                .exclusividad(condicion.isExclusividad())
                .createdAt(condicion.getCreatedAt())
                .build();
    }
}
