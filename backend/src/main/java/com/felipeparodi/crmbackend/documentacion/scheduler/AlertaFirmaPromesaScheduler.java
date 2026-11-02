package com.felipeparodi.crmbackend.documentacion.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.documentacion.entity.EstadoDocumento;
import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import com.felipeparodi.crmbackend.documentacion.repository.PromesaCompraventaRepository;
import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class AlertaFirmaPromesaScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaFirmaPromesaScheduler.class);

    private static final int DIAS_ANTICIPACION_ALERTA = 7;

    private final PromesaCompraventaRepository promesaCompraventaRepository;
    private final NotificacionRepository notificacionRepository;

    public AlertaFirmaPromesaScheduler(PromesaCompraventaRepository promesaCompraventaRepository,
                                        NotificacionRepository notificacionRepository) {
        this.promesaCompraventaRepository = promesaCompraventaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(cron = "0 30 6 * * *")
    @Transactional
    public void revisarFirmasProximas() {
        LocalDate fechaLimite = LocalDate.now().plusDays(DIAS_ANTICIPACION_ALERTA);
        List<PromesaCompraventa> promesas = promesaCompraventaRepository.findAll();

        for (PromesaCompraventa promesa : promesas) {
            try {
                if (promesa.isAlertaFirmaEnviada() || promesa.getEstado() == EstadoDocumento.CANCELADO) {
                    continue;
                }

                Negociacion negociacion = promesa.getNegociacion();
                LocalDate fechaFirmaPcv = negociacion.getFechaFirmaPcv();

                if (fechaFirmaPcv == null || fechaFirmaPcv.isAfter(fechaLimite)) {
                    continue;
                }

                String mensaje = "La firma de la escritura del negocio " + negociacion.getNumeroNegocio()
                        + " (promesa " + promesa.getNumeroContrato() + ") esta programada para el " + fechaFirmaPcv;

                Notificacion notificacion = Notificacion.builder()
                        .tipo(TipoNotificacion.FIRMA_PROMESA_PROXIMA)
                        .mensaje(mensaje)
                        .referenciaId(promesa.getId())
                        .build();

                notificacionRepository.save(notificacion);

                promesa.setAlertaFirmaEnviada(true);
                promesaCompraventaRepository.save(promesa);
            } catch (Exception ex) {
                log.error("Error al procesar alerta de firma para promesa {}", promesa.getId(), ex);
            }
        }
    }
}
