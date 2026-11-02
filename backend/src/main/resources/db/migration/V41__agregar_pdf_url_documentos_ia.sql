ALTER TABLE contrato_corretaje
    ADD COLUMN pdf_url VARCHAR(500);

ALTER TABLE promesa_compraventa
    ADD COLUMN pdf_url VARCHAR(500);

ALTER TABLE otrosi_promesa
    ADD COLUMN pdf_url VARCHAR(500);
