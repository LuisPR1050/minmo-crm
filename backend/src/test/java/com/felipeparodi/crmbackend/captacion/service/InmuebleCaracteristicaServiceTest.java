package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.AsignarCaracteristicasRequest;
import com.felipeparodi.crmbackend.captacion.dto.CaracteristicaResponse;
import com.felipeparodi.crmbackend.captacion.entity.Caracteristica;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.InmuebleCaracteristica;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.CaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InmuebleCaracteristicaServiceTest {

    @Mock
    private InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    @Mock
    private InmuebleRepository inmuebleRepository;
    @Mock
    private CaracteristicaRepository caracteristicaRepository;

    private InmuebleCaracteristicaService service;

    @BeforeEach
    void setUp() {
        service = new InmuebleCaracteristicaService(inmuebleCaracteristicaRepository, inmuebleRepository,
                caracteristicaRepository);
    }

    @Test
    void asignarOmiteCaracteristicasYaAsignadasYNoLasDuplica() {
        UUID inmuebleId = UUID.randomUUID();
        UUID caracteristicaYaAsignadaId = UUID.randomUUID();

        Inmueble inmueble = Inmueble.builder().id(inmuebleId).codigo("APT-000001").build();
        Caracteristica caracteristicaExistente = Caracteristica.builder()
                .id(caracteristicaYaAsignadaId).nombre("Piscina").categoria("Amenidad").build();

        InmuebleCaracteristica asignacionExistente = InmuebleCaracteristica.builder()
                .id(UUID.randomUUID())
                .inmueble(inmueble)
                .caracteristica(caracteristicaExistente)
                .build();

        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.of(inmueble));
        when(inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId))
                .thenReturn(List.of(asignacionExistente));

        AsignarCaracteristicasRequest request = new AsignarCaracteristicasRequest();
        request.setCaracteristicaIds(List.of(caracteristicaYaAsignadaId));

        List<CaracteristicaResponse> resultado = service.asignar(inmuebleId, request);

        assertThat(resultado).hasSize(1);
        verify(inmuebleCaracteristicaRepository, never()).save(any());
        verify(caracteristicaRepository, never()).findById(any());
    }

    @Test
    void asignarConsultaLasAsignacionesExistentesUnaSolaVezSinImportarCuantasCaracteristicasSePidan() {
        UUID inmuebleId = UUID.randomUUID();
        UUID caracteristicaId1 = UUID.randomUUID();
        UUID caracteristicaId2 = UUID.randomUUID();
        UUID caracteristicaId3 = UUID.randomUUID();

        Inmueble inmueble = Inmueble.builder().id(inmuebleId).codigo("APT-000001").build();

        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.of(inmueble));
        when(inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId)).thenReturn(List.of());
        when(caracteristicaRepository.findById(any())).thenAnswer(inv -> Optional.of(
                Caracteristica.builder().id(inv.getArgument(0)).nombre("Caracteristica").categoria("General").build()));
        when(inmuebleCaracteristicaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AsignarCaracteristicasRequest request = new AsignarCaracteristicasRequest();
        request.setCaracteristicaIds(List.of(caracteristicaId1, caracteristicaId2, caracteristicaId3));

        service.asignar(inmuebleId, request);

        // Se consulta una vez antes del bucle de asignacion (fix N+1) y una vez mas al
        // final para listar el resultado actualizado: 2 invocaciones totales, no una por caracteristica.
        verify(inmuebleCaracteristicaRepository, times(2)).findByInmuebleId(inmuebleId);
        verify(inmuebleCaracteristicaRepository, times(3)).save(any());
    }

    @Test
    void asignarNoDuplicaCuandoElMismoIdSeRepiteEnLaMismaSolicitud() {
        UUID inmuebleId = UUID.randomUUID();
        UUID caracteristicaId = UUID.randomUUID();

        Inmueble inmueble = Inmueble.builder().id(inmuebleId).codigo("APT-000001").build();

        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.of(inmueble));
        when(inmuebleCaracteristicaRepository.findByInmuebleId(inmuebleId)).thenReturn(List.of());
        when(caracteristicaRepository.findById(caracteristicaId)).thenReturn(Optional.of(
                Caracteristica.builder().id(caracteristicaId).nombre("Piscina").categoria("Amenidad").build()));
        when(inmuebleCaracteristicaRepository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        AsignarCaracteristicasRequest request = new AsignarCaracteristicasRequest();
        request.setCaracteristicaIds(List.of(caracteristicaId, caracteristicaId));

        service.asignar(inmuebleId, request);

        verify(inmuebleCaracteristicaRepository, times(1)).save(any());
    }

    @Test
    void asignarLanzaExcepcionCuandoElInmuebleNoExiste() {
        UUID inmuebleId = UUID.randomUUID();
        when(inmuebleRepository.findById(inmuebleId)).thenReturn(Optional.empty());

        AsignarCaracteristicasRequest request = new AsignarCaracteristicasRequest();
        request.setCaracteristicaIds(List.of(UUID.randomUUID()));

        assertThatThrownBy(() -> service.asignar(inmuebleId, request))
                .isInstanceOf(RecursoNoEncontradoException.class);
    }
}
