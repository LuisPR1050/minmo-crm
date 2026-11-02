CREATE TABLE punto_referencia (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL UNIQUE,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE inmueble_punto_referencia (
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    punto_referencia_id UUID NOT NULL REFERENCES punto_referencia(id),
    PRIMARY KEY (inmueble_id, punto_referencia_id)
);

CREATE INDEX idx_inmueble_punto_ref_inmueble ON inmueble_punto_referencia(inmueble_id);
CREATE INDEX idx_inmueble_punto_ref_punto ON inmueble_punto_referencia(punto_referencia_id);
