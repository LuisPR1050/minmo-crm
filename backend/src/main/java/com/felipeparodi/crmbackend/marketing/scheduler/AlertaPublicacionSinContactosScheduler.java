package com.felipeparodi.crmbackend.marketing.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.marketing.entity.EstadoPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import com.felipeparodi.crmbackend.marketing.repository.EstadisticaPortalRepository;
import com.felipeparodi.crmbackend.marketing.repository.HistorialPublicacionRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlertaPublicacionSinContactosScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaPublicacionSinContactosScheduler.class);

    private static final int DIAS_ANTES_DE_ALERTAR = 30;
    private static final int DIAS_ANTES_DE_REPUBLICAR = 45;

    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final EstadisticaPortalRepository estadisticaPortalRepository;
    private final HistorialPublicacionRepository historialPublicacionRepository;
    private final NotificacionRepository notificacionRepository;

    public AlertaPublicacionSinContactosScheduler(PublicacionInmuebleRepository publicacionInmuebleRepository,
                                                    EstadisticaPortalRepository estadisticaPortalRepository,
                                                    HistorialPublicacionRepository historialPublicacionRepository,
                                                    NotificacionRepository notificacionRepository) {
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.estadisticaPortalRepository = estadisticaPortalRepository;
        this.historialPublicacionRepository = historialPublicacionRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(cron = "0 0 8 * * *")
    @Transactional
    public void revisarPublicacionesAntiguas() {
        alertarSinContactos();
        republicarInactivas();
    }

    private void alertarSinContactos() {
        LocalDate fechaLimite = LocalDate.now().minusDays(DIAS_ANTES_DE_ALERTAR);

        List<PublicacionInmueble> candidatas = publicacionInmuebleRepository
                .findByEstadoAndAlertaSinContactosEnviadaFalseAndFechaPublicacionLessThanEqual(
                        EstadoPublicacion.ACTIVA, fechaLimite);

        for (PublicacionInmueble publicacion : candidatas) {
            try {
                if (totalContactos(publicacion) > 0) {
                    continue;
                }

                long dias = java.time.temporal.ChronoUnit.DAYS.between(publicacion.getFechaPublicacion(), LocalDate.now());

                String mensaje = "La publicacion del inmueble " + publicacion.getInmueble().getCodigo()
                        + " lleva " + dias + " dias publicada sin generar contactos.";

                Notificacion notificacion = Notificacion.builder()
                        .tipo(TipoNotificacion.PUBLICACION_ANTIGUA_SIN_CONTACTOS)
                        .mensaje(mensaje)
                        .referenciaId(publicacion.getId())
                        .build();

                notificacionRepository.save(notificacion);

                publicacion.setAlertaSinContactosEnviada(true);
                publicacionInmuebleRepository.save(publicacion);

                registrarHistorial(publicacion, "Alerta generada: publicacion antigua sin contactos");
            } catch (Exception ex) {
                log.error("Error al procesar alerta de publicacion sin contactos para publicacion {}", publicacion.getId(), ex);
            }
        }
    }

    private void republicarInactivas() {
        LocalDate fechaLimite = LocalDate.now().minusDays(DIAS_ANTES_DE_REPUBLICAR);

        List<PublicacionInmueble> candidatas = publicacionInmuebleRepository
                .findByEstadoAndAlertaSinContactosEnviadaTrueAndFechaPublicacionLessThanEqual(
                        EstadoPublicacion.ACTIVA, fechaLimite);

        for (PublicacionInmueble publicacion : candidatas) {
            try {
                if (totalContactos(publicacion) > 0) {
                    continue;
                }

                publicacion.setFechaPublicacion(LocalDate.now());
                publicacion.setAlertaSinContactosEnviada(false);
                publicacionInmuebleRepository.save(publicacion);

                registrarHistorial(publicacion, "Republicacion automatica por inactividad prolongada");
            } catch (Exception ex) {
                log.error("Error al republicar la publicacion {}", publicacion.getId(), ex);
            }
        }
    }

    private long totalContactos(PublicacionInmueble publicacion) {
        return estadisticaPortalRepository.sumContactosPorPublicacion(publicacion.getId());
    }

    private void registrarHistorial(PublicacionInmueble publicacion, String evento) {
        HistorialPublicacion historial = HistorialPublicacion.builder()
                .publicacionInmueble(publicacion)
                .evento(evento)
                .fecha(LocalDateTime.now())
                .build();

        historialPublicacionRepository.save(historial);
    }
}
