CREATE TABLE visita_captacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    asesor_id UUID,
    fecha_visita DATE NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_CONFIRMAR',
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_visita_captacion_inmueble ON visita_captacion(inmueble_id);
CREATE INDEX idx_visita_captacion_fecha ON visita_captacion(fecha_visita);