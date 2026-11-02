package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.InmuebleRecomendadoResponse;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResultadoVisitaRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ResultadoVisitaResponse;
import com.felipeparodi.crmbackend.atencioncliente.entity.EstadoVisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.IntencionCompra;
import com.felipeparodi.crmbackend.atencioncliente.entity.NivelInteres;
import com.felipeparodi.crmbackend.atencioncliente.entity.Objecion;
import com.felipeparodi.crmbackend.atencioncliente.entity.ProximaAccion;
import com.felipeparodi.crmbackend.atencioncliente.entity.RequerimientoCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisitaObjecion;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.atencioncliente.repository.ObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.RequerimientoClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaObjecionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class ResultadoVisitaService {

    private static final Logger log = LoggerFactory.getLogger(ResultadoVisitaService.class);
    private static final Set<NivelInteres> NIVELES_INTERES_BAJO =
            Set.of(NivelInteres.POCO_INTERESADO, NivelInteres.NO_INTERESADO);

    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final VisitaClienteRepository visitaClienteRepository;
    private final ObjecionRepository objecionRepository;
    private final ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository;
    private final RequerimientoClienteRepository requerimientoClienteRepository;
    private final RecomendacionInmuebleIAService recomendacionInmuebleIAService;

    public ResultadoVisitaService(ResultadoVisitaRepository resultadoVisitaRepository,
                                   VisitaClienteRepository visitaClienteRepository,
                                   ObjecionRepository objecionRepository,
                                   ResultadoVisitaObjecionRepository resultadoVisitaObjecionRepository,
                                   RequerimientoClienteRepository requerimientoClienteRepository,
                                   RecomendacionInmuebleIAService recomendacionInmuebleIAService) {
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.visitaClienteRepository = visitaClienteRepository;
        this.objecionRepository = objecionRepository;
        this.resultadoVisitaObjecionRepository = resultadoVisitaObjecionRepository;
        this.requerimientoClienteRepository = requerimientoClienteRepository;
        this.recomendacionInmuebleIAService = recomendacionInmuebleIAService;
    }

    public ResultadoVisitaResponse crear(ResultadoVisitaRequest request) {
        VisitaCliente visita = visitaClienteRepository.findById(request.getVisitaId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Visita no encontrada con id: " + request.getVisitaId()));

        ResultadoVisita resultado = resultadoVisitaRepository.findByVisitaId(request.getVisitaId())
                .orElseGet(() -> ResultadoVisita.builder().visita(visita).build());

        resultado.setNumeroVisita(request.getNumeroVisita());
        resultado.setEstado(EstadoVisitaCliente.valueOf(request.getEstado()));
        resultado.setNivelInteres(request.getNivelInteres() != null
                ? NivelInteres.valueOf(request.getNivelInteres()) : null);
        resultado.setIntencionCompra(request.getIntencionCompra() != null
                ? IntencionCompra.valueOf(request.getIntencionCompra()) : null);
        resultado.setProximaAccion(request.getProximaAccion() != null
                ? ProximaAccion.valueOf(request.getProximaAccion()) : null);
        resultado.setSeguimientoNumero(request.getSeguimientoNumero());
        resultado.setSeguimientoUnidad(request.getSeguimientoUnidad());
        resultado.setObservacionesAsesor(request.getObservacionesAsesor());

        ResultadoVisita guardado = resultadoVisitaRepository.save(resultado);

        resultadoVisitaObjecionRepository.deleteAll(
                resultadoVisitaObjecionRepository.findByResultadoVisitaId(guardado.getId()));
        resultadoVisitaObjecionRepository.flush();

        if (request.getObjecionIds() != null) {
            for (UUID objecionId : request.getObjecionIds()) {
                Objecion objecion = objecionRepository.findById(objecionId)
                        .orElseThrow(() -> new RecursoNoEncontradoException(
                                "Objecion no encontrada con id: " + objecionId));

                ResultadoVisitaObjecion vinculo = ResultadoVisitaObjecion.builder()
                        .resultadoVisita(guardado)
                        .objecion(objecion)
                        .build();

                resultadoVisitaObjecionRepository.save(vinculo);
            }
        }

        return mapearAResponse(guardado);
    }

    public ResultadoVisitaResponse buscarPorId(UUID id) {
        ResultadoVisita resultado = resultadoVisitaRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Resultado no encontrado con id: " + id));

        return mapearAResponse(resultado);
    }

    public ResultadoVisitaResponse buscarPorVisita(UUID visitaId) {
        ResultadoVisita resultado = resultadoVisitaRepository.findByVisitaId(visitaId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "No existe resultado de visita para la visita: " + visitaId));

        return mapearAResponse(resultado);
    }

    private ResultadoVisitaResponse mapearAResponse(ResultadoVisita resultado) {
        List<String> nombresObjeciones = resultadoVisitaObjecionRepository
                .findByResultadoVisitaId(resultado.getId())
                .stream()
                .map(rvo -> rvo.getObjecion().getNombre())
                .toList();

        return ResultadoVisitaResponse.builder()
                .id(resultado.getId())
                .visitaId(resultado.getVisita().getId())
                .numeroVisita(resultado.getNumeroVisita())
                .estado(resultado.getEstado().name())
                .nivelInteres(resultado.getNivelInteres() != null ? resultado.getNivelInteres().name() : null)
                .intencionCompra(resultado.getIntencionCompra() != null ? resultado.getIntencionCompra().name() : null)
                .proximaAccion(resultado.getProximaAccion() != null ? resultado.getProximaAccion().name() : null)
                .seguimientoNumero(resultado.getSeguimientoNumero())
                .seguimientoUnidad(resultado.getSeguimientoUnidad())
                .observacionesAsesor(resultado.getObservacionesAsesor())
                .objeciones(nombresObjeciones)
                .createdAt(resultado.getCreatedAt())
                .recomendacionesAlternativas(buscarRecomendacionesAlternativas(resultado))
                .resumenGeneradoIA(resultado.getResumenGeneradoIA())
                .build();
    }

    private List<InmuebleRecomendadoResponse> buscarRecomendacionesAlternativas(ResultadoVisita resultado) {
        if (!NIVELES_INTERES_BAJO.contains(resultado.getNivelInteres())) {
            return List.of();
        }

        UUID clienteId = resultado.getVisita().getCliente().getId();
        List<RequerimientoCliente> requerimientos = requerimientoClienteRepository.findByClienteId(clienteId);
        if (requerimientos.isEmpty()) {
            return List.of();
        }

        RequerimientoCliente masReciente = requerimientos.stream()
                .max(Comparator.comparing(RequerimientoCliente::getCreatedAt))
                .orElseThrow();

        try {
            return recomendacionInmuebleIAService.recomendar(masReciente.getId());
        } catch (RuntimeException e) {
            log.warn("No se pudieron generar recomendaciones alternativas para el resultado de visita {}: {}",
                    resultado.getId(), e.getMessage());
            return List.of();
        }
    }
}