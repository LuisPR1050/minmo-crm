CREATE TABLE visita_cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    codigo_visita VARCHAR(20) NOT NULL UNIQUE,
    estado VARCHAR(30) NOT NULL DEFAULT 'PENDIENTE_CONFIRMAR',
    tipo_visita VARCHAR(30) NOT NULL,
    fecha_visita DATE NOT NULL,
    hora_inicio TIME,
    hora_fin TIME,
    confirmada_por_cliente BOOLEAN NOT NULL DEFAULT false,
    confirmada_por_propietario BOOLEAN NOT NULL DEFAULT false,
    recomendaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE objecion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE resultado_visita (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    visita_id UUID NOT NULL UNIQUE REFERENCES visita_cliente(id),
    numero_visita SMALLINT NOT NULL DEFAULT 1,
    estado VARCHAR(30) NOT NULL,
    nivel_interes VARCHAR(30),
    intencion_compra VARCHAR(40),
    proxima_accion VARCHAR(40),
    seguimiento_numero SMALLINT,
    seguimiento_unidad VARCHAR(20),
    observaciones_asesor TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE resultado_visita_objecion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    resultado_visita_id UUID NOT NULL REFERENCES resultado_visita(id),
    objecion_id UUID NOT NULL REFERENCES objecion(id),
    UNIQUE (resultado_visita_id, objecion_id)
);

CREATE INDEX idx_visita_cliente_cliente ON visita_cliente(cliente_id);
CREATE INDEX idx_visita_cliente_inmueble ON visita_cliente(inmueble_id);
CREATE INDEX idx_resultado_visita_objecion ON resultado_visita_objecion(resultado_visita_id);

INSERT INTO objecion (nombre) VALUES
    ('Precio'), ('Ubicacion'), ('Area'), ('Distribucion'), ('Estado del inmueble'),
    ('Acabados'), ('Administracion'), ('Parqueadero'), ('Seguridad'), ('Financiamiento'), ('Otra');