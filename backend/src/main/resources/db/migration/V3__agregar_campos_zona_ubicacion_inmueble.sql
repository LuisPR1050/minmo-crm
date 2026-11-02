ALTER TABLE inmueble
    ADD COLUMN estrato_socioeconomico SMALLINT,
    ADD COLUMN uso_suelo VARCHAR(30),
    ADD COLUMN latitud NUMERIC(10,7),
    ADD COLUMN longitud NUMERIC(10,7),
    ADD COLUMN puntos_claves_cercanos TEXT;