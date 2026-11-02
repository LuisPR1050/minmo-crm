CREATE TABLE parte_negociacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    rol VARCHAR(20) NOT NULL,
    orden SMALLINT NOT NULL DEFAULT 1,
    tipo_documento VARCHAR(30),
    numero_documento VARCHAR(30),
    estado_civil VARCHAR(30),
    direccion VARCHAR(200),
    correo_electronico VARCHAR(150),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE bien_asociado (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    tipo VARCHAR(30) NOT NULL,
    matricula_inmobiliaria VARCHAR(50),
    referencia_catastral VARCHAR(50),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_parte_negociacion ON parte_negociacion(negociacion_id);
CREATE INDEX idx_bien_asociado_negociacion ON bien_asociado(negociacion_id);