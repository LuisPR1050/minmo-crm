ALTER TABLE inmueble ADD COLUMN descripcion_publica TEXT;
ALTER TABLE inmueble ADD COLUMN landing_activa BOOLEAN NOT NULL DEFAULT TRUE;
ALTER TABLE inmueble ADD COLUMN landing_mostrar_precio BOOLEAN NOT NULL DEFAULT TRUE;

CREATE TABLE video_inmueble (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    url TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_video_inmueble_inmueble_id ON video_inmueble(inmueble_id);
