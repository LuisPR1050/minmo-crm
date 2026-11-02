package com.felipeparodi.crmbackend.atencioncliente.service;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class SmsService {

    private static final Logger log = LoggerFactory.getLogger(SmsService.class);

    private final String numeroOrigen;
    private final boolean habilitado;

    public SmsService(@Value("${twilio.account-sid:}") String accountSid,
                       @Value("${twilio.auth-token:}") String authToken,
                       @Value("${twilio.numero-origen:}") String numeroOrigen) {
        this.numeroOrigen = numeroOrigen;
        this.habilitado = !accountSid.isBlank() && !authToken.isBlank() && !numeroOrigen.isBlank();

        if (this.habilitado) {
            Twilio.init(accountSid, authToken);
        } else {
            log.warn("Twilio no esta configurado (twilio.account-sid / twilio.auth-token / "
                    + "twilio.numero-origen). Los SMS de visitas no se enviaran.");
        }
    }

    public void enviar(String numeroDestino, String texto) {
        if (!habilitado) {
            log.warn("SMS no enviado a {} porque Twilio no esta configurado", numeroDestino);
            return;
        }
        if (numeroDestino == null || numeroDestino.isBlank()) {
            log.warn("SMS no enviado: el numero de destino esta vacio");
            return;
        }

        try {
            Message.creator(new PhoneNumber(normalizar(numeroDestino)), new PhoneNumber(numeroOrigen), texto).create();
        } catch (RuntimeException ex) {
            log.error("Error enviando SMS a {}: {}", numeroDestino, ex.getMessage());
        }
    }

    private String normalizar(String numero) {
        String limpio = numero.replaceAll("[^0-9+]", "");
        if (limpio.startsWith("+")) {
            return limpio;
        }
        return limpio.startsWith("57") ? "+" + limpio : "+57" + limpio;
    }
}
