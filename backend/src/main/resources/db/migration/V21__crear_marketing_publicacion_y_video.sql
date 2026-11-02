CREATE TABLE portal_inmobiliario (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE publicacion_inmueble (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    tipo_operacion VARCHAR(20) NOT NULL,
    mostrar_precio BOOLEAN NOT NULL DEFAULT true,
    mostrar_contacto BOOLEAN NOT NULL DEFAULT true,
    mostrar_correo BOOLEAN NOT NULL DEFAULT false,
    comision_compartida BOOLEAN NOT NULL DEFAULT false,
    destacada BOOLEAN NOT NULL DEFAULT false,
    descripcion_corta TEXT,
    descripcion_larga TEXT,
    descripcion_optimizada TEXT,
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    fecha_publicacion DATE,
    alerta_sin_contactos_enviada BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_publicacion_inmueble_inmueble ON publicacion_inmueble(inmueble_id);
CREATE INDEX idx_publicacion_inmueble_estado ON publicacion_inmueble(estado);

CREATE TABLE publicacion_inmueble_portal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_inmueble_id UUID NOT NULL REFERENCES publicacion_inmueble(id),
    portal_inmobiliario_id UUID NOT NULL REFERENCES portal_inmobiliario(id),
    estado VARCHAR(20) NOT NULL DEFAULT 'ACTIVA',
    UNIQUE (publicacion_inmueble_id, portal_inmobiliario_id)
);

CREATE TABLE foto_publicacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_inmueble_id UUID NOT NULL REFERENCES publicacion_inmueble(id),
    url VARCHAR(500) NOT NULL,
    es_principal BOOLEAN NOT NULL DEFAULT false,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE video_publicacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_inmueble_id UUID NOT NULL REFERENCES publicacion_inmueble(id),
    url VARCHAR(500) NOT NULL,
    tipo_origen VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE historial_publicacion (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_inmueble_id UUID NOT NULL REFERENCES publicacion_inmueble(id),
    evento VARCHAR(200) NOT NULL,
    fecha TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE estadistica_portal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    publicacion_inmueble_portal_id UUID NOT NULL REFERENCES publicacion_inmueble_portal(id),
    vistas INTEGER NOT NULL DEFAULT 0,
    contactos INTEGER NOT NULL DEFAULT 0,
    fecha_corte DATE NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_foto_publicacion ON foto_publicacion(publicacion_inmueble_id);
CREATE INDEX idx_video_publicacion ON video_publicacion(publicacion_inmueble_id);
CREATE INDEX idx_historial_publicacion ON historial_publicacion(publicacion_inmueble_id);
CREATE INDEX idx_estadistica_portal ON estadistica_portal(publicacion_inmueble_portal_id);

CREATE TABLE canal_publicacion_video (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE destino_video (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(40) NOT NULL UNIQUE
);

CREATE TABLE tipo_publico_video (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(60) NOT NULL UNIQUE
);

CREATE TABLE solicitud_video_marketing (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    objetivo_publicacion VARCHAR(40) NOT NULL,
    estilo_contenido VARCHAR(30),
    longitud VARCHAR(20),
    llamado_accion VARCHAR(40),
    logo_url VARCHAR(500),
    estado VARCHAR(20) NOT NULL DEFAULT 'BORRADOR',
    video_generado_url VARCHAR(500),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_solicitud_video_inmueble ON solicitud_video_marketing(inmueble_id);

CREATE TABLE solicitud_video_canal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    canal_publicacion_video_id UUID NOT NULL REFERENCES canal_publicacion_video(id),
    UNIQUE (solicitud_video_id, canal_publicacion_video_id)
);

CREATE TABLE solicitud_video_destino (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    destino_video_id UUID NOT NULL REFERENCES destino_video(id),
    UNIQUE (solicitud_video_id, destino_video_id)
);

CREATE TABLE solicitud_video_tipo_publico (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    tipo_publico_video_id UUID NOT NULL REFERENCES tipo_publico_video(id),
    UNIQUE (solicitud_video_id, tipo_publico_video_id)
);

CREATE TABLE foto_solicitud_video (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    url VARCHAR(500) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE video_fuente_solicitud (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    url VARCHAR(500) NOT NULL,
    tipo_origen VARCHAR(20) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_foto_solicitud_video ON foto_solicitud_video(solicitud_video_id);
CREATE INDEX idx_video_fuente_solicitud ON video_fuente_solicitud(solicitud_video_id);

INSERT INTO portal_inmobiliario (nombre) VALUES
    ('Pagina Web'), ('Fincaraiz'), ('Metrocuadrado'), ('Ciencuadras'),
    ('Mercado Libre'), ('Facebook Marketplace'), ('Grupo de Asesores Aliados');

INSERT INTO canal_publicacion_video (nombre) VALUES
    ('Instagram Feed'), ('Instagram Stories'), ('Instagram Reels'), ('Facebook'),
    ('Facebook Stories'), ('TikTok'), ('YouTube Shorts'), ('Estado de WhatsApp');

INSERT INTO destino_video (nombre) VALUES
    ('Reel'), ('Short'), ('TikTok'), ('Video horizontal'), ('Video vertical');

INSERT INTO tipo_publico_video (nombre) VALUES
    ('Familias'), ('Inversionistas'), ('Parejas jovenes'), ('Solteros'),
    ('Adultos mayores'), ('Profesionales'), ('Empresarios'),
    ('Colombianos en el exterior'), ('Compradores de vivienda VIS'),
    ('Compradores de vivienda de lujo');
