ALTER TABLE solicitud_video_marketing
    ADD COLUMN titulo_generado VARCHAR(200),
    ADD COLUMN subtitulo_generado VARCHAR(200),
    ADD COLUMN descripcion_generada TEXT,
    ADD COLUMN llamado_accion_texto VARCHAR(200),
    ADD COLUMN hashtags TEXT,
    ADD COLUMN fecha_generacion_copy TIMESTAMP;

CREATE TABLE copia_video_canal (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    solicitud_video_id UUID NOT NULL REFERENCES solicitud_video_marketing(id),
    canal_publicacion_video_id UUID NOT NULL REFERENCES canal_publicacion_video(id),
    texto TEXT NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE INDEX idx_copia_video_canal ON copia_video_canal(solicitud_video_id);
