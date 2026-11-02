
package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.PropietarioRequest;
import com.felipeparodi.crmbackend.captacion.dto.PropietarioResponse;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class PropietarioService {

    private final PropietarioRepository propietarioRepository;
    private final InmuebleRepository inmuebleRepository;

    public PropietarioService(PropietarioRepository propietarioRepository, InmuebleRepository inmuebleRepository) {
        this.propietarioRepository = propietarioRepository;
        this.inmuebleRepository = inmuebleRepository;
    }

    public PropietarioResponse crear(PropietarioRequest request) {
        Propietario propietario = Propietario.builder()
                .nombre(request.getNombre())
                .telefonoPrincipal(request.getTelefonoPrincipal())
                .whatsapp(request.getWhatsapp())
                .correoElectronico(request.getCorreoElectronico())
                .tipoContacto(request.getTipoContacto())
                .tipoIncentivo(request.getTipoIncentivo())
                .valorIncentivo(request.getValorIncentivo())
                .referenciadorNombre(request.getReferenciadorNombre())
                .referenciadorTelefono(request.getReferenciadorTelefono())
                .build();

        Propietario guardado = propietarioRepository.save(propietario);

        return mapearAResponse(guardado);
    }

    public List<PropietarioResponse> listarTodos() {
        return propietarioRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public PropietarioResponse buscarPorId(UUID id) {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con id: " + id));

        return mapearAResponse(propietario);
    }

    public PropietarioResponse actualizar(UUID id, PropietarioRequest request) {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con id: " + id));

        propietario.setNombre(request.getNombre());
        propietario.setTelefonoPrincipal(request.getTelefonoPrincipal());
        propietario.setWhatsapp(request.getWhatsapp());
        propietario.setCorreoElectronico(request.getCorreoElectronico());
        propietario.setTipoContacto(request.getTipoContacto());
        propietario.setTipoIncentivo(request.getTipoIncentivo());
        propietario.setValorIncentivo(request.getValorIncentivo());
        propietario.setReferenciadorNombre(request.getReferenciadorNombre());
        propietario.setReferenciadorTelefono(request.getReferenciadorTelefono());

        Propietario actualizado = propietarioRepository.save(propietario);

        return mapearAResponse(actualizado);
    }

    public void eliminar(UUID id) {
        Propietario propietario = propietarioRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Propietario no encontrado con id: " + id));

        if (inmuebleRepository.existsByPropietarioId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el propietario porque tiene inmuebles asociados");
        }

        propietarioRepository.delete(propietario);
    }

    private PropietarioResponse mapearAResponse(Propietario propietario) {
        return PropietarioResponse.builder()
                .id(propietario.getId())
                .nombre(propietario.getNombre())
                .telefonoPrincipal(propietario.getTelefonoPrincipal())
                .whatsapp(propietario.getWhatsapp())
                .correoElectronico(propietario.getCorreoElectronico())
                .tipoContacto(propietario.getTipoContacto())
                .tipoIncentivo(propietario.getTipoIncentivo())
                .valorIncentivo(propietario.getValorIncentivo())
                .referenciadorNombre(propietario.getReferenciadorNombre())
                .referenciadorTelefono(propietario.getReferenciadorTelefono())
                .createdAt(propietario.getCreatedAt())
                .build();
    }
}