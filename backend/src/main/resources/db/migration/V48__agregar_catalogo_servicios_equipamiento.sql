-- Catalogo para la nueva pestana "Servicios y equipamiento" del inmueble (documento
-- "AJUSTES A REALIZAR", punto 23). Reutiliza la misma tabla caracteristica/inmueble_caracteristica
-- con 3 categorias nuevas.

INSERT INTO caracteristica (nombre, categoria) VALUES
    -- Elementos incluidos
    ('Cortinas', 'ELEMENTO_INCLUIDO'), ('Aires acondicionados', 'ELEMENTO_INCLUIDO'),
    ('Calentador', 'ELEMENTO_INCLUIDO'), ('Persianas', 'ELEMENTO_INCLUIDO'), ('Blackouts', 'ELEMENTO_INCLUIDO'),
    ('Ventiladores', 'ELEMENTO_INCLUIDO'), ('Lámparas', 'ELEMENTO_INCLUIDO'), ('Espejos', 'ELEMENTO_INCLUIDO'),
    ('Muebles empotrados', 'ELEMENTO_INCLUIDO'), ('Campana extractora', 'ELEMENTO_INCLUIDO'),
    ('Horno', 'ELEMENTO_INCLUIDO'), ('Estufa', 'ELEMENTO_INCLUIDO'), ('Nevera', 'ELEMENTO_INCLUIDO'),
    ('Mueble de TV', 'ELEMENTO_INCLUIDO'), ('Divisiones de baño', 'ELEMENTO_INCLUIDO'),
    ('Cerraduras electrónicas', 'ELEMENTO_INCLUIDO'), ('Cámaras de seguridad', 'ELEMENTO_INCLUIDO'),
    ('Iluminación LEDS', 'ELEMENTO_INCLUIDO'), ('Cerca eléctrica', 'ELEMENTO_INCLUIDO'), ('Otros', 'ELEMENTO_INCLUIDO'),

    -- Servicios
    ('Portería', 'SERVICIO_EDIFICIO'), ('Vigilancia privada', 'SERVICIO_EDIFICIO'),
    ('Control de acceso', 'SERVICIO_EDIFICIO'), ('Recepción', 'SERVICIO_EDIFICIO'),
    ('Citofonía', 'SERVICIO_EDIFICIO'), ('Videoportero', 'SERVICIO_EDIFICIO'),
    ('Cámaras de seguridad', 'SERVICIO_EDIFICIO'),

    -- Servicios publicos / infraestructura
    ('Acueducto', 'SERVICIO_PUBLICO'), ('Alcantarillado', 'SERVICIO_PUBLICO'),
    ('Energía eléctrica', 'SERVICIO_PUBLICO'), ('Gas natural', 'SERVICIO_PUBLICO'),
    ('Internet', 'SERVICIO_PUBLICO'), ('Internet en zonas comunes', 'SERVICIO_PUBLICO'),
    ('Planta eléctrica', 'SERVICIO_PUBLICO'), ('Tanque de reserva de agua', 'SERVICIO_PUBLICO'),
    ('Bombas de agua', 'SERVICIO_PUBLICO'), ('Sistema contra incendios', 'SERVICIO_PUBLICO');
