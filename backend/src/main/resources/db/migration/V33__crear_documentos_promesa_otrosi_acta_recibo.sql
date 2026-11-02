-- Nota: documento_promesa_compraventa, documento_otrosi_promesa, documento_acta_entrega y
-- documento_recibo_pago ya estan definidas en V16, V17, V19 y V20 respectivamente. Esta
-- migracion existe unicamente para reparar entornos donde esas tablas quedaron sin crear
-- (por edicion posterior de esas migraciones ya aplicadas); usa IF NOT EXISTS para no fallar
-- en una base de datos nueva donde V16/V17/V19/V20 ya las crearon.

CREATE TABLE IF NOT EXISTS documento_promesa_compraventa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promesa_compraventa_id UUID NOT NULL REFERENCES promesa_compraventa(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS documento_otrosi_promesa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    otrosi_promesa_id UUID NOT NULL REFERENCES otrosi_promesa(id),
    url VARCHAR(500) NOT NULL,
    descripcion VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS documento_acta_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS documento_recibo_pago (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    recibo_pago_id UUID NOT NULL REFERENCES recibo_pago(id),
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX IF NOT EXISTS idx_documento_promesa_compraventa ON documento_promesa_compraventa(promesa_compraventa_id);
CREATE INDEX IF NOT EXISTS idx_documento_otrosi_promesa ON documento_otrosi_promesa(otrosi_promesa_id);
CREATE INDEX IF NOT EXISTS idx_documento_acta_entrega ON documento_acta_entrega(acta_entrega_id);
CREATE INDEX IF NOT EXISTS idx_documento_recibo_pago ON documento_recibo_pago(recibo_pago_id);
