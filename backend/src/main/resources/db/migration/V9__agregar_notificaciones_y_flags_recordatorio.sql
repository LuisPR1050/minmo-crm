ALTER TABLE visita_cliente
    ADD COLUMN recordatorio_24h_enviado BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN recordatorio_2h_enviado BOOLEAN NOT NULL DEFAULT false;

ALTER TABLE resultado_visita
    ADD COLUMN seguimiento_alerta_enviada BOOLEAN NOT NULL DEFAULT false;

CREATE TABLE notificacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    tipo VARCHAR(40) NOT NULL,
    mensaje TEXT NOT NULL,
    referencia_id UUID NOT NULL,
    leida BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_notificacion_leida ON notificacion(leida);