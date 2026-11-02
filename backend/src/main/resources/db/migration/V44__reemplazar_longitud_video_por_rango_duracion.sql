ALTER TABLE solicitud_video_marketing
    DROP COLUMN longitud,
    ADD COLUMN duracion_minima_minutos DOUBLE PRECISION,
    ADD COLUMN duracion_maxima_minutos DOUBLE PRECISION;
