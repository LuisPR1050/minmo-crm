CREATE SEQUENCE contrato_corretaje_numero_seq START WITH 1 INCREMENT BY 1;

CREATE TABLE contrato_corretaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    numero_contrato VARCHAR(20) NOT NULL UNIQUE,
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    propietario_id UUID NOT NULL REFERENCES propietario(id),
    fecha_elaboracion DATE NOT NULL,
    ciudad VARCHAR(100),
    tipo_contrato VARCHAR(20) NOT NULL,
    precio_autorizado_venta NUMERIC(14,2),
    precio_minimo_autorizado NUMERIC(14,2),
    porcentaje_comision NUMERIC(6,3),
    valor_fijo_comision NUMERIC(14,2),
    sobreprecio_comision NUMERIC(14,2),
    forma_pago_comision VARCHAR(30),
    fecha_vigencia_hasta DATE,
    opcion_firma VARCHAR(30),
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    alerta_vencimiento_enviada BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_contrato_corretaje_inmueble ON contrato_corretaje(inmueble_id);
CREATE INDEX idx_contrato_corretaje_propietario ON contrato_corretaje(propietario_id);
CREATE INDEX idx_contrato_corretaje_estado ON contrato_corretaje(estado);

CREATE TABLE clausula_corretaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(120) NOT NULL UNIQUE
);

CREATE TABLE contrato_corretaje_clausula (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contrato_corretaje_id UUID NOT NULL REFERENCES contrato_corretaje(id),
    clausula_corretaje_id UUID NOT NULL REFERENCES clausula_corretaje(id),
    incluida BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (contrato_corretaje_id, clausula_corretaje_id)
);

CREATE TABLE autorizacion_propietario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(80) NOT NULL UNIQUE
);

CREATE TABLE contrato_corretaje_autorizacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contrato_corretaje_id UUID NOT NULL REFERENCES contrato_corretaje(id),
    autorizacion_propietario_id UUID NOT NULL REFERENCES autorizacion_propietario(id),
    autorizada BOOLEAN NOT NULL DEFAULT true,
    UNIQUE (contrato_corretaje_id, autorizacion_propietario_id)
);

CREATE TABLE documento_contrato_corretaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contrato_corretaje_id UUID NOT NULL REFERENCES contrato_corretaje(id),
    tipo VARCHAR(30) NOT NULL,
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE historial_contrato_corretaje (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    contrato_corretaje_id UUID NOT NULL REFERENCES contrato_corretaje(id),
    usuario_id UUID,
    evento VARCHAR(200) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_documento_contrato_corretaje ON documento_contrato_corretaje(contrato_corretaje_id);
CREATE INDEX idx_historial_contrato_corretaje ON historial_contrato_corretaje(contrato_corretaje_id);

INSERT INTO clausula_corretaje (nombre) VALUES
    ('Identificacion del inmueble'),
    ('Precio autorizado'),
    ('Vigencia del contrato'),
    ('Exclusividad'),
    ('Honorarios del corredor'),
    ('Momento en que se causa la comision'),
    ('Proteccion contra elusion'),
    ('Obligaciones del propietario'),
    ('Obligaciones del corredor'),
    ('Llaves del inmueble'),
    ('Documentacion'),
    ('Confidencialidad'),
    ('Penalidad'),
    ('Merito ejecutivo'),
    ('Medios electronicos'),
    ('Notificaciones'),
    ('Terminacion'),
    ('Clausula de reconocimiento del cliente presentado'),
    ('Clausula de evidencia digital');

INSERT INTO autorizacion_propietario (nombre) VALUES
    ('Publicar fotografias'),
    ('Publicar videos'),
    ('Realizar recorridos virtuales'),
    ('Utilizar dron'),
    ('Instalar aviso'),
    ('Compartir con asesores aliados'),
    ('Compartir con inmobiliarias aliadas'),
    ('Publicar en portales inmobiliarios'),
    ('Promocionar en redes sociales');
