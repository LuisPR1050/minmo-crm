CREATE SEQUENCE negocio_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE negociacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_negocio VARCHAR(20) NOT NULL UNIQUE,
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    propietario_id UUID NOT NULL REFERENCES propietario(id),
    asesor_id UUID,
    fecha_inicio DATE NOT NULL,
    fecha_firma_pcv DATE,
    precio_inicial_publicado NUMERIC(14,2),
    oferta_inicial_comprador NUMERIC(14,2),
    contraoferta_vendedor NUMERIC(14,2),
    precio_final_acordado NUMERIC(14,2),
    entidad_financiera VARCHAR(100),
    financiacion_directa_condiciones TEXT,
    permuta_condiciones TEXT,
    estado_fisico_entrega VARCHAR(30),
    momento_entrega VARCHAR(40),
    escritura_firmada BOOLEAN NOT NULL DEFAULT false,
    pago_recibido BOOLEAN NOT NULL DEFAULT false,
    entrega_realizada BOOLEAN NOT NULL DEFAULT false,
    estado VARCHAR(30) NOT NULL DEFAULT 'NEGOCIACION_INICIADA',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_negociacion_inmueble ON negociacion(inmueble_id);
CREATE INDEX idx_negociacion_cliente ON negociacion(cliente_id);
CREATE INDEX idx_negociacion_estado ON negociacion(estado);