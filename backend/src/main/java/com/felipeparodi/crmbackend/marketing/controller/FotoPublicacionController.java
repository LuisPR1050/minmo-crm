package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.marketing.dto.FotoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.FotoPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.service.FotoPublicacionService;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
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
@RequestMapping("/api/publicaciones-inmuebles/{publicacionInmuebleId}/fotos")
public class FotoPublicacionController {

    private final FotoPublicacionService fotoPublicacionService;

    public FotoPublicacionController(FotoPublicacionService fotoPublicacionService) {
        this.fotoPublicacionService = fotoPublicacionService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public FotoPublicacionResponse agregar(@PathVariable UUID publicacionInmuebleId,
                                             @RequestBody FotoPublicacionRequest request) {
        return fotoPublicacionService.agregar(publicacionInmuebleId, request);
    }

    @GetMapping
    public List<FotoPublicacionResponse> listar(@PathVariable UUID publicacionInmuebleId) {
        return fotoPublicacionService.listarPorPublicacion(publicacionInmuebleId);
    }

    @PostMapping("/subir")
    @ResponseStatus(HttpStatus.CREATED)
    public FotoPublicacionResponse subir(@PathVariable UUID publicacionInmuebleId,
                                          @RequestParam("foto") MultipartFile foto,
                                          @RequestParam(value = "esPrincipal", defaultValue = "false") boolean esPrincipal) {
        return fotoPublicacionService.subir(publicacionInmuebleId, foto, esPrincipal);
    }
}
