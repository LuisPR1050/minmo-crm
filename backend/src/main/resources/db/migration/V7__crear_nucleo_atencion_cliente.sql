CREATE TABLE cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL,
    telefono VARCHAR(20) NOT NULL,
    whatsapp VARCHAR(20),
    correo_electronico VARCHAR(150),
    tipo_contacto VARCHAR(30) NOT NULL,
    subtipo_contacto VARCHAR(50),
    numero_contacto VARCHAR(20),
    asesor_id UUID,
    estado_pipeline VARCHAR(30) NOT NULL DEFAULT 'CONTACTO_INICIADO',
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE requerimiento_cliente (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    cliente_id UUID NOT NULL REFERENCES cliente(id),
    tipo_propiedad VARCHAR(30),
    ciudad VARCHAR(50),
    zona VARCHAR(50),
    entorno VARCHAR(30),
    sitios_claves_cercanos TEXT,
    presupuesto_min NUMERIC(14,2),
    presupuesto_max NUMERIC(14,2),
    forma_pago VARCHAR(30),
    tiempo_para_comprar VARCHAR(20),
    numero_habitaciones SMALLINT,
    numero_banos SMALLINT,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_requerimiento_cliente ON requerimiento_cliente(cliente_id);
CREATE INDEX idx_cliente_estado_pipeline ON cliente(estado_pipeline);