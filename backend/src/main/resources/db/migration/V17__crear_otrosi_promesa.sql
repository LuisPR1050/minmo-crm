CREATE SEQUENCE otrosi_promesa_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE otrosi_promesa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_otrosi VARCHAR(20) NOT NULL UNIQUE,
    promesa_compraventa_id UUID NOT NULL REFERENCES promesa_compraventa(id),
    fecha_elaboracion DATE NOT NULL,
    motivo VARCHAR(40) NOT NULL,
    otro_motivo_texto VARCHAR(300),
    justificacion VARCHAR(50),
    nuevo_precio NUMERIC(14,2),
    nueva_fecha_escritura DATE,
    nueva_fecha_entrega DATE,
    nueva_distribucion_gastos TEXT,
    nuevos_bienes_incluidos TEXT,
    nuevas_condiciones_especiales TEXT,
    observaciones TEXT,
    aplicado BOOLEAN NOT NULL DEFAULT false,
    fecha_aplicacion TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_otrosi_promesa_promesa ON otrosi_promesa(promesa_compraventa_id);

CREATE TABLE otrosi_promesa_clausula (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    otrosi_promesa_id UUID NOT NULL REFERENCES otrosi_promesa(id),
    clausula_corretaje_id UUID NOT NULL REFERENCES clausula_corretaje(id),
    incluida BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (otrosi_promesa_id, clausula_corretaje_id)
);

CREATE TABLE documento_otrosi_promesa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    otrosi_promesa_id UUID NOT NULL REFERENCES otrosi_promesa(id),
    url VARCHAR(500) NOT NULL,
    descripcion VARCHAR(200),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_otrosi_promesa ON documento_otrosi_promesa(otrosi_promesa_id);
