ALTER TABLE inmueble
    ADD COLUMN direccion_exacta VARCHAR(255);

CREATE SEQUENCE acta_entrega_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE acta_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_acta VARCHAR(20) NOT NULL UNIQUE,
    negociacion_id UUID NOT NULL UNIQUE REFERENCES negociacion(id),
    promesa_compraventa_id UUID REFERENCES promesa_compraventa(id),
    fecha_elaboracion DATE NOT NULL,
    fecha_hora_entrega TIMESTAMP,
    ciudad VARCHAR(100),
    lectura_energia VARCHAR(50),
    lectura_agua VARCHAR(50),
    lectura_gas VARCHAR(50),
    danos_existentes TEXT,
    reparaciones_pendientes TEXT,
    acuerdos_posteriores TEXT,
    compromisos_asumidos TEXT,
    declaracion_comprador VARCHAR(20),
    vendedor_entrega_conforme BOOLEAN NOT NULL DEFAULT false,
    observaciones TEXT,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_acta_entrega_promesa ON acta_entrega(promesa_compraventa_id);

CREATE TABLE condicion_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE acta_entrega_condicion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    condicion_entrega_id UUID NOT NULL REFERENCES condicion_entrega(id),
    cumple BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (acta_entrega_id, condicion_entrega_id)
);

CREATE TABLE acta_entrega_item_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    item_entrega_id UUID NOT NULL REFERENCES item_entrega(id),
    entregado BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (acta_entrega_id, item_entrega_id)
);

CREATE TABLE acceso_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE acta_entrega_acceso (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    acceso_entrega_id UUID NOT NULL REFERENCES acceso_entrega(id),
    entregado BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (acta_entrega_id, acceso_entrega_id)
);

CREATE TABLE paz_y_salvo_tipo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE acta_entrega_paz_y_salvo (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    paz_y_salvo_tipo_id UUID NOT NULL REFERENCES paz_y_salvo_tipo(id),
    al_dia BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (acta_entrega_id, paz_y_salvo_tipo_id)
);

CREATE TABLE documento_acta_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    acta_entrega_id UUID NOT NULL REFERENCES acta_entrega(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_acta_entrega ON documento_acta_entrega(acta_entrega_id);

INSERT INTO condicion_entrega (nombre) VALUES
    ('Desocupado'), ('Habitado'), ('Limpio'), ('Pintado'), ('Remodelado'),
    ('En el estado pactado'), ('Libre de ocupantes'), ('Libre de deudas conocidas'),
    ('Con servicios publicos activos');

INSERT INTO acceso_entrega (nombre) VALUES
    ('Llaves principales'), ('Llaves auxiliares'), ('Control remoto'),
    ('Tarjetas de acceso'), ('Claves de ingreso'), ('Controles electronicos');

INSERT INTO paz_y_salvo_tipo (nombre) VALUES
    ('Impuesto predial'), ('Administracion'), ('Energia'), ('Agua'), ('Gas'),
    ('Valorizacion'), ('Otros');
