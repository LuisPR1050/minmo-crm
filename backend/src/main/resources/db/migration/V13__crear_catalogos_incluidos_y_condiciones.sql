CREATE TABLE item_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE negociacion_item_entrega (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    item_entrega_id UUID NOT NULL REFERENCES item_entrega(id),
    incluido BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (negociacion_id, item_entrega_id)
);

CREATE TABLE condicion_especial (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE negociacion_condicion_especial (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    negociacion_id UUID NOT NULL REFERENCES negociacion(id),
    condicion_especial_id UUID NOT NULL REFERENCES condicion_especial(id),
    UNIQUE (negociacion_id, condicion_especial_id)
);

CREATE INDEX idx_neg_item_entrega ON negociacion_item_entrega(negociacion_id);
CREATE INDEX idx_neg_condicion_especial ON negociacion_condicion_especial(negociacion_id);

INSERT INTO item_entrega (nombre) VALUES
    ('Aires acondicionados'), ('Cortinas'), ('Blackout'), ('Closets'),
    ('Calentador'), ('Cocina integral'), ('Horno'), ('Campana'),
    ('Lamparas'), ('Ventiladores'), ('Muebles'), ('Otro');

INSERT INTO condicion_especial (nombre) VALUES
    ('Sujeto a aprobacion de credito'),
    ('Sujeto a venta de otra propiedad'),
    ('Sujeto a levantamiento de patrimonio familiar'),
    ('Sujeto a sucesion'),
    ('Sujeto a cancelacion de hipoteca'),
    ('Sujeto a saneamiento documental');