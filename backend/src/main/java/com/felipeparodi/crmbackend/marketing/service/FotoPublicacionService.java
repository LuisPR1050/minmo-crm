package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.marketing.dto.FotoPublicacionRequest;
import com.felipeparodi.crmbackend.marketing.dto.FotoPublicacionResponse;
import com.felipeparodi.crmbackend.marketing.entity.FotoPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.marketing.repository.FotoPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Service
public class FotoPublicacionService {

    private static final String SUBCARPETA_PUBLICACIONES = "publicaciones";

    private final FotoPublicacionRepository fotoPublicacionRepository;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;

    public FotoPublicacionService(FotoPublicacionRepository fotoPublicacionRepository,
                                   PublicacionInmuebleRepository publicacionInmuebleRepository,
                                   AlmacenamientoArchivosService almacenamientoArchivosService) {
        this.fotoPublicacionRepository = fotoPublicacionRepository;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
    }

    public FotoPublicacionResponse agregar(UUID publicacionInmuebleId, FotoPublicacionRequest request) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionInmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionInmuebleId));

        if (request.isEsPrincipal()) {
            desmarcarPrincipalesActuales(publicacionInmuebleId);
        }

        FotoPublicacion foto = FotoPublicacion.builder()
                .publicacionInmueble(publicacion)
                .url(request.getUrl())
                .esPrincipal(request.isEsPrincipal())
                .build();

        FotoPublicacion guardada = fotoPublicacionRepository.save(foto);

        return mapearAResponse(guardada);
    }

    public FotoPublicacionResponse subir(UUID publicacionInmuebleId, MultipartFile foto, boolean esPrincipal) {
        PublicacionInmueble publicacion = publicacionInmuebleRepository.findById(publicacionInmuebleId)
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Publicacion no encontrada con id: " + publicacionInmuebleId));

        if (foto == null || foto.isEmpty()) {
            throw new IllegalArgumentException("Debes seleccionar una imagen");
        }

        String rutaPublica = almacenamientoArchivosService.guardarImagen(SUBCARPETA_PUBLICACIONES, foto);

        boolean marcarComoPrincipal = esPrincipal || fotoPublicacionRepository.findByPublicacionInmuebleId(publicacionInmuebleId).isEmpty();

        if (marcarComoPrincipal) {
            desmarcarPrincipalesActuales(publicacionInmuebleId);
        }

        FotoPublicacion nuevaFoto = FotoPublicacion.builder()
                .publicacionInmueble(publicacion)
                .url(rutaPublica)
                .esPrincipal(marcarComoPrincipal)
                .build();

        FotoPublicacion guardada = fotoPublicacionRepository.save(nuevaFoto);

        return mapearAResponse(guardada);
    }

    public List<FotoPublicacionResponse> listarPorPublicacion(UUID publicacionInmuebleId) {
        return fotoPublicacionRepository.findByPublicacionInmuebleId(publicacionInmuebleId)
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    private void desmarcarPrincipalesActuales(UUID publicacionInmuebleId) {
        List<FotoPublicacion> fotos = fotoPublicacionRepository.findByPublicacionInmuebleId(publicacionInmuebleId);

        for (FotoPublicacion foto : fotos) {
            if (foto.isEsPrincipal()) {
                foto.setEsPrincipal(false);
                fotoPublicacionRepository.save(foto);
            }
        }
    }

    private FotoPublicacionResponse mapearAResponse(FotoPublicacion foto) {
        return FotoPublicacionResponse.builder()
                .id(foto.getId())
                .url(foto.getUrl())
                .esPrincipal(foto.isEsPrincipal())
                .createdAt(foto.getCreatedAt())
                .build();
    }
}
