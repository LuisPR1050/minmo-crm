CREATE SEQUENCE recibo_pago_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE recibo_pago (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_recibo VARCHAR(20) NOT NULL UNIQUE,
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    promesa_compraventa_id UUID REFERENCES promesa_compraventa(id),
    plan_de_pago_id UUID REFERENCES plan_de_pago(id),
    fecha_elaboracion DATE NOT NULL,
    concepto_pago VARCHAR(40) NOT NULL,
    numero_cuota SMALLINT,
    valor_recibido NUMERIC(14,2) NOT NULL,
    saldo_pendiente NUMERIC(14,2),
    moneda VARCHAR(10),
    forma_pago VARCHAR(30),
    pago_realizado_por VARCHAR(30),
    pago_recibido_por VARCHAR(30),
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_recibo_pago_negociacion ON recibo_pago(negociacion_id);
CREATE INDEX idx_recibo_pago_promesa ON recibo_pago(promesa_compraventa_id);

CREATE TABLE documento_recibo_pago (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recibo_pago_id UUID NOT NULL REFERENCES recibo_pago(id),
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_recibo_pago ON documento_recibo_pago(recibo_pago_id);
