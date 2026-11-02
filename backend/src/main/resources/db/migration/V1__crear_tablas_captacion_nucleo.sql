CREATE TABLE propietario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    telefono_principal VARCHAR(20) NOT NULL,
    whatsapp VARCHAR(20),
    correo_electronico VARCHAR(150),
    tipo_contacto VARCHAR(30) NOT NULL,
    tipo_incentivo VARCHAR(20),
    valor_incentivo NUMERIC(12,2),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE inmueble (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    propietario_id UUID NOT NULL REFERENCES propietario(id),
    codigo VARCHAR(20) NOT NULL UNIQUE,
    tipo_propiedad VARCHAR(20) NOT NULL,
    ciudad VARCHAR(50) NOT NULL,
    zona VARCHAR(50) NOT NULL,
    entorno VARCHAR(30),
    area_total NUMERIC(10,2),
    area_construida NUMERIC(10,2),
    numero_habitaciones SMALLINT,
    numero_banos SMALLINT,
    tipo_cocina VARCHAR(30),
    precio_minimo_aceptado NUMERIC(14,2),
    precio_sugerido NUMERIC(14,2),
    estado_juridico VARCHAR(30) NOT NULL DEFAULT 'LIBRE',
    estado_captacion VARCHAR(30) NOT NULL DEFAULT 'PROSPECTO',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE condicion_captacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL UNIQUE REFERENCES inmueble(id),
    asesor_id UUID NOT NULL,
    tipo_comision VARCHAR(30) NOT NULL,
    valor_comision NUMERIC(12,2) NOT NULL,
    exclusividad BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE archivo_adjunto (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    tipo VARCHAR(20) NOT NULL,
    url VARCHAR(500) NOT NULL,
    es_principal BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE historial_captacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    usuario_id UUID,
    evento VARCHAR(200) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_inmueble_propietario ON inmueble(propietario_id);
CREATE INDEX idx_inmueble_estado_captacion ON inmueble(estado_captacion);
CREATE INDEX idx_archivo_inmueble ON archivo_adjunto(inmueble_id);
CREATE INDEX idx_historial_inmueble ON historial_captacion(inmueble_id);