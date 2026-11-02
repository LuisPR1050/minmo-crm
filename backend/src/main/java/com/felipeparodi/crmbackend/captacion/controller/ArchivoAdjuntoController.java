package com.felipeparodi.crmbackend.captacion.controller;

import com.felipeparodi.crmbackend.captacion.dto.ArchivoAdjuntoRequest;
import com.felipeparodi.crmbackend.captacion.dto.ArchivoAdjuntoResponse;
import com.felipeparodi.crmbackend.captacion.service.ArchivoAdjuntoService;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/archivos-adjuntos")
public class ArchivoAdjuntoController {

    private final ArchivoAdjuntoService archivoAdjuntoService;

    public ArchivoAdjuntoController(ArchivoAdjuntoService archivoAdjuntoService) {
        this.archivoAdjuntoService = archivoAdjuntoService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoAdjuntoResponse crear(@RequestBody ArchivoAdjuntoRequest request) {
        return archivoAdjuntoService.crear(request);
    }

    @PostMapping("/inmuebles/{inmuebleId}/fotos")
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoAdjuntoResponse subirFoto(@PathVariable UUID inmuebleId,
                                             @RequestParam("foto") MultipartFile foto,
                                             @RequestParam(value = "esPrincipal", defaultValue = "false") boolean esPrincipal) {
        return archivoAdjuntoService.subirFoto(inmuebleId, foto, esPrincipal);
    }

    @PostMapping("/inmuebles/{inmuebleId}/documentos")
    @ResponseStatus(HttpStatus.CREATED)
    public ArchivoAdjuntoResponse subirDocumento(@PathVariable UUID inmuebleId,
                                                  @RequestParam("documento") MultipartFile documento,
                                                  @RequestParam("tipo") String tipo) {
        return archivoAdjuntoService.subirDocumento(inmuebleId, documento, tipo);
    }

    @GetMapping
    public List<ArchivoAdjuntoResponse> listar(@RequestParam(required = false) UUID inmuebleId) {
        if (inmuebleId != null) {
            return archivoAdjuntoService.listarPorInmueble(inmuebleId);
        }
        return archivoAdjuntoService.listarTodos();
    }

    @GetMapping("/{id}")
    public ArchivoAdjuntoResponse buscarPorId(@PathVariable UUID id) {
        return archivoAdjuntoService.buscarPorId(id);
    }

    @PatchMapping("/{id}/marcar-principal")
    public ArchivoAdjuntoResponse marcarComoPrincipal(@PathVariable UUID id) {
        return archivoAdjuntoService.marcarComoPrincipal(id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id) {
        archivoAdjuntoService.eliminar(id);
    }
}
