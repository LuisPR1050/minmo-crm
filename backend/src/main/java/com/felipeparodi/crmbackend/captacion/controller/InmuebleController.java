package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.AnalisisObjecionesInmuebleResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.AnalisisObjecionesInmuebleIAService;
import com.felipeparodi.crmbackend.captacion.dto.ActualizarLandingInmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.BusquedaInmuebleIARequest;
import com.felipeparodi.crmbackend.captacion.dto.CambiarEstadoCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.ClienteSugeridoResponse;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import com.felipeparodi.crmbackend.captacion.dto.PrecioSugeridoResponse;
import com.felipeparodi.crmbackend.captacion.service.BusquedaInmuebleIAService;
import com.felipeparodi.crmbackend.captacion.service.ClienteSugeridoIAService;
import com.felipeparodi.crmbackend.captacion.service.GanchoEmocionalIAService;
import com.felipeparodi.crmbackend.captacion.service.InmuebleService;
import com.felipeparodi.crmbackend.captacion.service.PrecioSugeridoIAService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/inmuebles")
public class InmuebleController {

    private final InmuebleService inmuebleService;
    private final BusquedaInmuebleIAService busquedaInmuebleIAService;
    private final PrecioSugeridoIAService precioSugeridoIAService;
    private final AnalisisObjecionesInmuebleIAService analisisObjecionesInmuebleIAService;
    private final ClienteSugeridoIAService clienteSugeridoIAService;
    private final GanchoEmocionalIAService ganchoEmocionalIAService;

    public InmuebleController(InmuebleService inmuebleService,
                               BusquedaInmuebleIAService busquedaInmuebleIAService,
                               PrecioSugeridoIAService precioSugeridoIAService,
                               AnalisisObjecionesInmuebleIAService analisisObjecionesInmuebleIAService,
                               ClienteSugeridoIAService clienteSugeridoIAService,
                               GanchoEmocionalIAService ganchoEmocionalIAService) {
        this.inmuebleService = inmuebleService;
        this.busquedaInmuebleIAService = busquedaInmuebleIAService;
        this.precioSugeridoIAService = precioSugeridoIAService;
        this.analisisObjecionesInmuebleIAService = analisisObjecionesInmuebleIAService;
        this.clienteSugeridoIAService = clienteSugeridoIAService;
        this.ganchoEmocionalIAService = ganchoEmocionalIAService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InmuebleResponse crear(@jakarta.validation.Valid @RequestBody InmuebleRequest request) {
        return inmuebleService.crear(request);
    }

    @GetMapping
    public List<InmuebleResponse> listarTodos() {
        return inmuebleService.listarTodos();
    }

    @PostMapping("/busqueda-ia")
    public List<InmuebleResponse> buscarConIA(@RequestBody BusquedaInmuebleIARequest request) {
        return busquedaInmuebleIAService.buscar(request.getConsulta());
    }

    @GetMapping("/{id}")
    public InmuebleResponse buscarPorId(@PathVariable UUID id) {
        return inmuebleService.buscarPorId(id);
    }

    @PatchMapping("/{id}")
    public InmuebleResponse actualizar(@PathVariable UUID id, @jakarta.validation.Valid @RequestBody InmuebleRequest request) {
        return inmuebleService.actualizar(id, request);
    }

    @PostMapping("/{id}/precio-sugerido-ia")
    public PrecioSugeridoResponse sugerirPrecioConIA(@PathVariable UUID id) {
        return precioSugeridoIAService.sugerir(id);
    }

    @PostMapping("/{id}/analisis-objeciones-ia")
    public AnalisisObjecionesInmuebleResponse analizarObjecionesConIA(@PathVariable UUID id) {
        return analisisObjecionesInmuebleIAService.analizar(id);
    }

    @PostMapping("/{id}/clientes-sugeridos-ia")
    public List<ClienteSugeridoResponse> sugerirClientesConIA(@PathVariable UUID id) {
        return clienteSugeridoIAService.sugerir(id);
    }

    @PatchMapping("/{id}/estado-captacion")
    public InmuebleResponse cambiarEstadoCaptacion(@PathVariable UUID id,
                                                    @RequestBody CambiarEstadoCaptacionRequest request) {
        return inmuebleService.cambiarEstadoCaptacion(id, request);
    }

    @PatchMapping("/{id}/landing")
    public InmuebleResponse actualizarLanding(@PathVariable UUID id,
                                               @RequestBody ActualizarLandingInmuebleRequest request) {
        return inmuebleService.actualizarLanding(id, request);
    }

    @PostMapping("/{id}/gancho-emocional-ia")
    public InmuebleResponse generarGanchoEmocionalConIA(@PathVariable UUID id) {
        return ganchoEmocionalIAService.generar(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id, @RequestParam(defaultValue = "false") boolean confirmar) {
        inmuebleService.eliminar(id, confirmar);
    }
}