package com.felipeparodi.crmbackend.documentacion.service;

import com.felipeparodi.crmbackend.documentacion.dto.AsignarAutorizacionesPropietarioRequest;
import com.felipeparodi.crmbackend.documentacion.dto.AutorizacionPropietarioResponse;
import com.felipeparodi.crmbackend.documentacion.entity.AutorizacionPropietario;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeAutorizacion;
import com.felipeparodi.crmbackend.documentacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.documentacion.repository.AutorizacionPropietarioRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeAutorizacionRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ContratoCorretajeAutorizacionService {

    private final ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository;
    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final AutorizacionPropietarioRepository autorizacionPropietarioRepository;

    public ContratoCorretajeAutorizacionService(ContratoCorretajeAutorizacionRepository contratoCorretajeAutorizacionRepository,
                                                 ContratoCorretajeRepository contratoCorretajeRepository,
                                                 AutorizacionPropietarioRepository autorizacionPropietarioRepository) {
        this.contratoCorretajeAutorizacionRepository = contratoCorretajeAutorizacionRepository;
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.autorizacionPropietarioRepository = autorizacionPropietarioRepository;
    }

    public List<AutorizacionPropietarioResponse> asignar(UUID contratoCorretajeId,
                                                           AsignarAutorizacionesPropietarioRequest request) {
        ContratoCorretaje contrato = contratoCorretajeRepository.findById(contratoCorretajeId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Contrato de corretaje no encontrado con id: " + contratoCorretajeId));

        List<ContratoCorretajeAutorizacion> yaAsignadas = contratoCorretajeAutorizacionRepository
                .findByContratoCorretajeId(contratoCorretajeId);

        for (UUID autorizacionId : request.getAutorizacionIds()) {
            boolean yaAsignada = yaAsignadas.stream()
                    .anyMatch(ca -> ca.getAutorizacionPropietario().getId().equals(autorizacionId));

            if (yaAsignada) {
                continue;
            }

            AutorizacionPropietario autorizacion = autorizacionPropietarioRepository.findById(autorizacionId)
                    .orElseThrow(() -> new RecursoNoEncontradoException(
                            "Autorizacion del propietario no encontrada con id: " + autorizacionId));

            ContratoCorretajeAutorizacion nuevaAsignacion = ContratoCorretajeAutorizacion.builder()
                    .contratoCorretaje(contrato)
                    .autorizacionPropietario(autorizacion)
                    .autorizada(true)
                    .build();

            contratoCorretajeAutorizacionRepository.save(nuevaAsignacion);
        }

        return listarPorContrato(contratoCorretajeId);
    }

    public List<AutorizacionPropietarioResponse> listarPorContrato(UUID contratoCorretajeId) {
        return contratoCorretajeAutorizacionRepository.findByContratoCorretajeId(contratoCorretajeId)
                .stream()
                .filter(ContratoCorretajeAutorizacion::isAutorizada)
                .map(ca -> AutorizacionPropietarioResponse.builder()
                        .id(ca.getAutorizacionPropietario().getId())
                        .nombre(ca.getAutorizacionPropietario().getNombre())
                        .build())
                .toList();
    }
}
