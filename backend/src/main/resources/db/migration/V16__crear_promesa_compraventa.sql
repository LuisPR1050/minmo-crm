CREATE SEQUENCE promesa_compraventa_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE promesa_compraventa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_contrato VARCHAR(20) NOT NULL UNIQUE,
    negociacion_id UUID NOT NULL UNIQUE REFERENCES negociacion(id),
    fecha_elaboracion DATE NOT NULL,
    ciudad VARCHAR(100),
    opcion_firma VARCHAR(30),
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    alerta_firma_enviada BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_promesa_compraventa_estado ON promesa_compraventa(estado);

CREATE TABLE clausula_promesa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(150) NOT NULL UNIQUE
);

CREATE TABLE promesa_compraventa_clausula (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promesa_compraventa_id UUID NOT NULL REFERENCES promesa_compraventa(id),
    clausula_promesa_id UUID NOT NULL REFERENCES clausula_promesa(id),
    incluida BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (promesa_compraventa_id, clausula_promesa_id)
);

CREATE TABLE documento_promesa_compraventa (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    promesa_compraventa_id UUID NOT NULL REFERENCES promesa_compraventa(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_promesa_compraventa ON documento_promesa_compraventa(promesa_compraventa_id);

INSERT INTO clausula_promesa (nombre) VALUES
    ('Identificacion de las partes'),
    ('Identificacion del inmueble'),
    ('Objeto del contrato'),
    ('Tradicion del inmueble'),
    ('Precio y condiciones de pago'),
    ('Firma de la escritura'),
    ('Entrega del inmueble'),
    ('Estado juridico del inmueble y obligacion de levantar hipotecas, patrimonio de familia o afectacion a vivienda familiar'),
    ('Cancelacion de gravamenes'),
    ('Estudio de titulos'),
    ('Avaluo'),
    ('Creditos hipotecarios'),
    ('Obligaciones del vendedor'),
    ('Obligaciones del comprador'),
    ('Gastos'),
    ('Arras confirmatorias y penalidad por incumplimiento'),
    ('Resolucion del contrato'),
    ('Merito ejecutivo'),
    ('Notificaciones'),
    ('Prevencion del lavado de activos'),
    ('Entrega anticipada en calidad de arrendamiento'),
    ('Reglamento de propiedad horizontal'),
    ('Clausula sobre bienes comunes'),
    ('Procesos judiciales de la copropiedad'),
    ('Estado de conservacion'),
    ('No elusion, comision e intermediacion');
