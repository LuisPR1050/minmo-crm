CREATE TABLE plan_de_pago (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    tipo_recurso VARCHAR(30) NOT NULL,
    numero_cuota SMALLINT NOT NULL,
    porcentaje NUMERIC(5,2),
    valor NUMERIC(14,2),
    momento_pago VARCHAR(40),
    metodo_pago VARCHAR(30),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE distribucion_gastos (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL UNIQUE REFERENCES negociacion(id),
    gastos_escrituracion VARCHAR(20),
    retencion_fuente VARCHAR(20),
    gastos_registro VARCHAR(20),
    gastos_cancelacion_gravamen VARCHAR(20),
    gastos_constitucion_gravamen VARCHAR(20),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_plan_pago_negociacion ON plan_de_pago(negociacion_id);
