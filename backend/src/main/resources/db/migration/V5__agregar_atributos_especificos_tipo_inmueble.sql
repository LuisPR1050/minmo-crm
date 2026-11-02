ALTER TABLE inmueble
    ADD COLUMN situada VARCHAR(20),
    ADD COLUMN niveles VARCHAR(20),
    ADD COLUMN piso SMALLINT,
    ADD COLUMN vista VARCHAR(20),
    ADD COLUMN tipo_acceso VARCHAR(20),
    ADD COLUMN medida_frente NUMERIC(8,2),
    ADD COLUMN medida_fondo NUMERIC(8,2),
    ADD COLUMN otras_caracteristicas_lote TEXT,
    ADD COLUMN quien_ocupa_actualmente VARCHAR(30);