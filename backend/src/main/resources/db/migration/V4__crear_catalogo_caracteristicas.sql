CREATE TABLE caracteristica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    nombre VARCHAR(100) NOT NULL,
    categoria VARCHAR(30) NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT now(),
    UNIQUE (nombre, categoria)
);

CREATE TABLE inmueble_caracteristica (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    inmueble_id UUID NOT NULL REFERENCES inmueble(id),
    caracteristica_id UUID NOT NULL REFERENCES caracteristica(id),
    UNIQUE (inmueble_id, caracteristica_id)
);

CREATE INDEX idx_inmueble_caract_inmueble ON inmueble_caracteristica(inmueble_id);
CREATE INDEX idx_inmueble_caract_caract ON inmueble_caracteristica(caracteristica_id);

-- Precargamos el catalogo con las opciones de tu documento
INSERT INTO caracteristica (nombre, categoria) VALUES
    ('Piscina', 'ZONA_COMUN'), ('Salon social', 'ZONA_COMUN'), ('Gimnasio', 'ZONA_COMUN'),
    ('Zona infantil', 'ZONA_COMUN'), ('Canchas deportivas', 'ZONA_COMUN'), ('Zonas verdes', 'ZONA_COMUN'),
    ('Senderos peatonales', 'ZONA_COMUN'), ('Zona BBQ', 'ZONA_COMUN'), ('Terraza social', 'ZONA_COMUN'),
    ('Parque para mascotas', 'ZONA_COMUN'), ('Circuito cerrado de camaras', 'ZONA_COMUN'),
    ('Parqueadero de visitantes', 'ZONA_COMUN'), ('Ascensor', 'ZONA_COMUN'),
    ('Porteria y vigilancia 24 horas', 'SERVICIO'), ('Energia', 'SERVICIO'), ('Gas', 'SERVICIO'),
    ('Agua', 'SERVICIO'), ('Aseo', 'SERVICIO'), ('Alcantarillado', 'SERVICIO'), ('Internet', 'SERVICIO'),
    ('Sala', 'DISTRIBUCION_INTERNA'), ('Comedor', 'DISTRIBUCION_INTERNA'), ('Baño social', 'DISTRIBUCION_INTERNA'),
    ('Patio', 'DISTRIBUCION_INTERNA'), ('Balcon', 'DISTRIBUCION_INTERNA'), ('Terraza', 'DISTRIBUCION_INTERNA'),
    ('Garaje', 'DISTRIBUCION_INTERNA'), ('Vestier', 'DISTRIBUCION_INTERNA'),
    ('Closets', 'ADICIONAL'), ('Cerca electrica', 'ADICIONAL'), ('Camaras de seguridad', 'ADICIONAL'),
    ('Aire acondicionado', 'ADICIONAL'), ('Calentador de agua', 'ADICIONAL'), ('Iluminacion LED', 'ADICIONAL');