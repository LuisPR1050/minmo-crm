ALTER TABLE negociacion
    ADD COLUMN fecha_entrega_programada DATE;

ALTER TABLE plan_de_pago
    ADD COLUMN fecha_esperada DATE,
    ADD COLUMN pagada BOOLEAN NOT NULL DEFAULT false,
    ADD COLUMN alerta_pago_tardio_enviada BOOLEAN NOT NULL DEFAULT false;
