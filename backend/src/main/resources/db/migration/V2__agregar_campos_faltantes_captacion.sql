-- Campos faltantes en inmueble (información comercial y física)
ALTER TABLE inmueble
    ADD COLUMN matricula_inmobiliaria VARCHAR(50),
    ADD COLUMN referencia_catastral VARCHAR(50),
    ADD COLUMN area_privada NUMERIC(10,2),
    ADD COLUMN estado_fisico VARCHAR(30),
    ADD COLUMN expectativa_precio NUMERIC(14,2),
    ADD COLUMN valor_administracion NUMERIC(12,2),
    ADD COLUMN valor_arriendo NUMERIC(12,2),
    ADD COLUMN valor_impuestos NUMERIC(12,2),
    ADD COLUMN valor_adeudado NUMERIC(14,2),
    ADD COLUMN escucha_ofertas BOOLEAN NOT NULL DEFAULT true;

-- Campo faltante en condicion_captacion
ALTER TABLE condicion_captacion
    ADD COLUMN whatsapp_asesor VARCHAR(20);

-- Secuencia para generar el consecutivo del codigo de inmueble automaticamente
CREATE SEQUENCE inmueble_codigo_seq START WITH 1 INCREMENT BY 1;

-- Checklist juridico detallado (hipoteca, embargo, patrimonio de familia, vivienda familiar)
CREATE TABLE checklist_juridico (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL UNIQUE REFERENCES inmueble(id),
    tiene_hipoteca BOOLEAN NOT NULL DEFAULT false,
    valor_hipoteca NUMERIC(14,2),
    tiene_embargo BOOLEAN NOT NULL DEFAULT false,
    valor_embargo NUMERIC(14,2),
    tiene_patrimonio_familia BOOLEAN NOT NULL DEFAULT false,
    tiene_vivienda_familiar BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_checklist_inmueble ON checklist_juridico(inmueble_id);