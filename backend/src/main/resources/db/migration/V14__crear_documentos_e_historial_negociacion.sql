CREATE TABLE documento_negociacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE historial_negociacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    usuario_id UUID,
    evento VARCHAR(200) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_negociacion ON documento_negociacion(negociacion_id);
CREATE INDEX idx_historial_negociacion ON historial_negociacion(negociacion_id);