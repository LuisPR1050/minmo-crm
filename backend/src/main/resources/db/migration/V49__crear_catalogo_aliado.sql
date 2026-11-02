CREATE TABLE aliado (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20),
    tipo VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

ALTER TABLE condicion_captacion
    ADD COLUMN aliado_id UUID REFERENCES aliado(id);
