package com.felipeparodi.crmbackend.documentacion.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.EstadoDocumento;
import com.felipeparodi.crmbackend.documentacion.entity.HistorialContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import com.felipeparodi.crmbackend.documentacion.repository.HistorialContratoCorretajeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlertaVencimientoContratoCorretajeScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaVencimientoContratoCorretajeScheduler.class);

    private static final int DIAS_ANTICIPACION_ALERTA = 30;

    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final HistorialContratoCorretajeRepository historialContratoCorretajeRepository;
    private final NotificacionRepository notificacionRepository;

    public AlertaVencimientoContratoCorretajeScheduler(ContratoCorretajeRepository contratoCorretajeRepository,
                                                         HistorialContratoCorretajeRepository historialContratoCorretajeRepository,
                                                         NotificacionRepository notificacionRepository) {
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.historialContratoCorretajeRepository = historialContratoCorretajeRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(cron = "0 0 6 * * *")
    @Transactional
    public void revisarVencimientos() {
        alertarProximosAVencer();
        marcarVencidos();
    }

    private void alertarProximosAVencer() {
        LocalDate fechaLimite = LocalDate.now().plusDays(DIAS_ANTICIPACION_ALERTA);

        List<ContratoCorretaje> proximosAVencer = contratoCorretajeRepository
                .findByEstadoAndAlertaVencimientoEnviadaFalseAndFechaVigenciaHastaLessThanEqual(
                        EstadoDocumento.VIGENTE, fechaLimite);

        for (ContratoCorretaje contrato : proximosAVencer) {
            try {
                String mensaje = "El contrato de corretaje " + contrato.getNumeroContrato()
                        + " del inmueble " + contrato.getInmueble().getCodigo()
                        + " vence el " + contrato.getFechaVigenciaHasta();

                Notificacion notificacion = Notificacion.builder()
                        .tipo(TipoNotificacion.VENCIMIENTO_CONTRATO_CORRETAJE)
                        .mensaje(mensaje)
                        .referenciaId(contrato.getId())
                        .build();

                notificacionRepository.save(notificacion);

                contrato.setAlertaVencimientoEnviada(true);
                contratoCorretajeRepository.save(contrato);
            } catch (Exception ex) {
                log.error("Error al procesar alerta de vencimiento para contrato de corretaje {}", contrato.getId(), ex);
            }
        }
    }

    private void marcarVencidos() {
        List<ContratoCorretaje> vencidos = contratoCorretajeRepository
                .findByEstadoAndFechaVigenciaHastaLessThan(EstadoDocumento.VIGENTE, LocalDate.now());

        for (ContratoCorretaje contrato : vencidos) {
            try {
                contrato.setEstado(EstadoDocumento.VENCIDO);
                contratoCorretajeRepository.save(contrato);

                HistorialContratoCorretaje historial = HistorialContratoCorretaje.builder()
                        .contratoCorretaje(contrato)
                        .evento("Vencimiento automatico: estado cambiado a VENCIDO")
                        .fecha(LocalDateTime.now())
                        .build();

                historialContratoCorretajeRepository.save(historial);
            } catch (Exception ex) {
                log.error("Error al marcar como vencido el contrato de corretaje {}", contrato.getId(), ex);
            }
        }
    }
}
