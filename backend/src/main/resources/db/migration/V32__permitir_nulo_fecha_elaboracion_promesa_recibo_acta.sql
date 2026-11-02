ALTER TABLE promesa_compraventa ALTER COLUMN fecha_elaboracion DROP NOT NULL;
ALTER TABLE recibo_pago ALTER COLUMN fecha_elaboracion DROP NOT NULL;
ALTER TABLE acta_entrega ALTER COLUMN fecha_elaboracion DROP NOT NULL;
