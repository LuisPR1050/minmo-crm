package com.felipeparodi.crmbackend.marketing.service;

import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteRequest;
import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.atencioncliente.service.ClienteService;
import com.felipeparodi.crmbackend.marketing.dto.LeadPortalWebhookRequest;
import com.felipeparodi.crmbackend.marketing.entity.EstadisticaPortal;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueblePortal;
import com.felipeparodi.crmbackend.marketing.repository.EstadisticaPortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmueblePortalRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

@Service
@Transactional
public class LeadPortalWebhookService {

    private static final Pattern PATRON_CORREO = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    private final ClienteService clienteService;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final PublicacionInmueblePortalRepository publicacionInmueblePortalRepository;
    private final EstadisticaPortalRepository estadisticaPortalRepository;
    private final HistorialPublicacionRepository historialPublicacionRepository;

    public LeadPortalWebhookService(ClienteService clienteService,
                                     PublicacionInmuebleRepository publicacionInmuebleRepository,
                                     PublicacionInmueblePortalRepository publicacionInmueblePortalRepository,
                                     EstadisticaPortalRepository estadisticaPortalRepository,
                                     HistorialPublicacionRepository historialPublicacionRepository) {
        this.clienteService = clienteService;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.publicacionInmueblePortalRepository = publicacionInmueblePortalRepository;
        this.estadisticaPortalRepository = estadisticaPortalRepository;
        this.historialPublicacionRepository = historialPublicacionRepository;
    }

    public ClienteResponse recibirContacto(LeadPortalWebhookRequest request) {
        validarRequest(request);

        ClienteRequest clienteRequest = new ClienteRequest();
        clienteRequest.setNombre(request.getNombreContacto());
        clienteRequest.setTelefono(request.getTelefonoContacto());
        clienteRequest.setCorreoElectronico(request.getCorreoContacto());
        clienteRequest.setTipoContacto("PORTAL");
        clienteRequest.setSubtipoContacto(request.getPortalNombre());

        ClienteResponse clienteCreado = clienteService.crear(clienteRequest);

        if (request.getPublicacionInmuebleId() != null) {
            publicacionInmuebleRepository.findById(request.getPublicacionInmuebleId())
                    .ifPresent(publicacion -> registrarLeadEnPublicacion(publicacion, request, clienteCreado));
        }

        return clienteCreado;
    }

    private void validarRequest(LeadPortalWebhookRequest request) {
        if (request.getNombreContacto() == null || request.getNombreContacto().isBlank()) {
            throw new IllegalArgumentException("El nombre del contacto es obligatorio");
        }

        if (request.getTelefonoContacto() == null || request.getTelefonoContacto().isBlank()) {
            throw new IllegalArgumentException("El telefono del contacto es obligatorio");
        }

        if (request.getCorreoContacto() != null && !request.getCorreoContacto().isBlank()
                && !PATRON_CORREO.matcher(request.getCorreoContacto()).matches()) {
            throw new IllegalArgumentException("El correo del contacto no tiene un formato valido");
        }
    }

    private void registrarLeadEnPublicacion(PublicacionInmueble publicacion,
                                             LeadPortalWebhookRequest request,
                                             ClienteResponse clienteCreado) {
        HistorialPublicacion historial = HistorialPublicacion.builder()
                .publicacionInmueble(publicacion)
                .evento("Lead recibido desde " + request.getPortalNombre() + ": " + clienteCreado.getNombre())
                .fecha(LocalDateTime.now())
                .build();

        historialPublicacionRepository.save(historial);

        List<PublicacionInmueblePortal> vinculos = publicacionInmueblePortalRepository
                .findByPublicacionInmuebleId(publicacion.getId());

        vinculos.stream()
                .filter(v -> v.getPortalInmobiliario().getNombre().equalsIgnoreCase(request.getPortalNombre()))
                .findFirst()
                .ifPresent(vinculo -> {
                    EstadisticaPortal estadistica = EstadisticaPortal.builder()
                            .publicacionInmueblePortal(vinculo)
                            .vistas(0)
                            .contactos(1)
                            .fechaCorte(LocalDate.now())
                            .build();

                    estadisticaPortalRepository.save(estadistica);
                });
    }
}
