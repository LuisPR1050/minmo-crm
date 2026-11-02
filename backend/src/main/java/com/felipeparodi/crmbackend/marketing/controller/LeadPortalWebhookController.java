package com.felipeparodi.crmbackend.marketing.controller;

import com.felipeparodi.crmbackend.atencioncliente.dto.ClienteResponse;
import com.felipeparodi.crmbackend.marketing.dto.LeadPortalWebhookRequest;
import com.felipeparodi.crmbackend.marketing.service.LeadPortalWebhookService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

@RestController
@RequestMapping("/api/webhooks/portales")
public class LeadPortalWebhookController {

    private static final Logger log = LoggerFactory.getLogger(LeadPortalWebhookController.class);
    private static final String ENCABEZADO_SECRETO = "X-Webhook-Secret";

    private final LeadPortalWebhookService leadPortalWebhookService;
    private final String secretoConfigurado;

    public LeadPortalWebhookController(LeadPortalWebhookService leadPortalWebhookService,
                                        @Value("${webhooks.portales.secret:}") String secretoConfigurado) {
        this.leadPortalWebhookService = leadPortalWebhookService;
        this.secretoConfigurado = secretoConfigurado;
        if (secretoConfigurado == null || secretoConfigurado.isBlank()) {
            log.warn("webhooks.portales.secret no esta configurado: /api/webhooks/portales/leads "
                    + "rechazara todas las solicitudes hasta que se configure la variable de entorno "
                    + "WEBHOOKS_PORTALES_SECRET y el portal envie el encabezado {}.",
                    ENCABEZADO_SECRETO);
        }
    }

    @PostMapping("/leads")
    @ResponseStatus(HttpStatus.CREATED)
    public ClienteResponse recibirContacto(@RequestBody LeadPortalWebhookRequest request,
                                            @RequestHeader(value = ENCABEZADO_SECRETO, required = false) String secretoRecibido) {
        validarSecreto(secretoRecibido);
        return leadPortalWebhookService.recibirContacto(request);
    }

    private void validarSecreto(String secretoRecibido) {
        if (secretoConfigurado == null || secretoConfigurado.isBlank()) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Este webhook aun no esta configurado (falta WEBHOOKS_PORTALES_SECRET)");
        }
        boolean coincide = secretoRecibido != null
                && MessageDigest.isEqual(
                        secretoRecibido.getBytes(StandardCharsets.UTF_8),
                        secretoConfigurado.getBytes(StandardCharsets.UTF_8));
        if (!coincide) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Secreto de webhook invalido");
        }
    }
}
