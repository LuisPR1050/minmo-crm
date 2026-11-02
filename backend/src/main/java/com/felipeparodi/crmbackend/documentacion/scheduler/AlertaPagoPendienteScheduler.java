package com.felipeparodi.crmbackend.documentacion.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
import com.felipeparodi.crmbackend.negociacion.repository.PlanDePagoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Component
public class AlertaPagoPendienteScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaPagoPendienteScheduler.class);

    private final PlanDePagoRepository planDePagoRepository;
    private final NotificacionRepository notificacionRepository;

    public AlertaPagoPendienteScheduler(PlanDePagoRepository planDePagoRepository,
                                         NotificacionRepository notificacionRepository) {
        this.planDePagoRepository = planDePagoRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(cron = "0 0 7 * * *")
    @Transactional
    public void revisarPagosPendientes() {
        List<PlanDePago> pagosVencidosSinRegistrar = planDePagoRepository
                .findByPagadaFalseAndAlertaPagoTardioEnviadaFalseAndFechaEsperadaLessThan(LocalDate.now());

        for (PlanDePago plan : pagosVencidosSinRegistrar) {
            try {
                String mensaje = "El pago pactado para el negocio " + plan.getNegociacion().getNumeroNegocio()
                        + " (" + plan.getTipoRecurso() + ") no se ha registrado y venció el " + plan.getFechaEsperada();

                Notificacion notificacion = Notificacion.builder()
                        .tipo(TipoNotificacion.PAGO_PENDIENTE_VENCIDO)
                        .mensaje(mensaje)
                        .referenciaId(plan.getId())
                        .build();

                notificacionRepository.save(notificacion);

                plan.setAlertaPagoTardioEnviada(true);
                planDePagoRepository.save(plan);
            } catch (Exception ex) {
                log.error("Error al procesar alerta de pago pendiente para plan de pago {}", plan.getId(), ex);
            }
        }
    }
}
