-- Amplia el catalogo de caracteristicas con la taxonomia detallada de zonas/areas que pidio
-- el cliente (documento "AJUSTES A REALIZAR", punto 22). No se tocan las filas existentes de
-- las 4 categorias originales (ZONA_COMUN, SERVICIO, DISTRIBUCION_INTERNA, ADICIONAL) para no
-- perder las asignaciones ya guardadas en inmueble_caracteristica.

INSERT INTO caracteristica (nombre, categoria) VALUES
    -- Areas sociales de la vivienda
    ('Antesala', 'AREA_SOCIAL'), ('Sala', 'AREA_SOCIAL'), ('Comedor', 'AREA_SOCIAL'),
    ('Sala-comedor', 'AREA_SOCIAL'), ('Sala de estar', 'AREA_SOCIAL'), ('Sala de TV', 'AREA_SOCIAL'),
    ('Estudio', 'AREA_SOCIAL'), ('Oficina', 'AREA_SOCIAL'), ('Balcón', 'AREA_SOCIAL'),
    ('Terraza', 'AREA_SOCIAL'), ('Patio', 'AREA_SOCIAL'), ('Jardín interior', 'AREA_SOCIAL'),

    -- Areas de cocina y servicio
    ('Cocina', 'AREA_COCINA_SERVICIO'), ('Zona de labores', 'AREA_COCINA_SERVICIO'),
    ('Despensa', 'AREA_COCINA_SERVICIO'), ('Alacena', 'AREA_COCINA_SERVICIO'),
    ('Cuarto de servicio', 'AREA_COCINA_SERVICIO'), ('Baño de servicio', 'AREA_COCINA_SERVICIO'),

    -- Areas privadas
    ('Habitación principal', 'AREA_PRIVADA'), ('Habitaciones auxiliares', 'AREA_PRIVADA'),
    ('Vestier', 'AREA_PRIVADA'), ('Clósets', 'AREA_PRIVADA'), ('Baño privado', 'AREA_PRIVADA'),
    ('Baño auxiliar', 'AREA_PRIVADA'), ('Baño social', 'AREA_PRIVADA'), ('Baño de servicios', 'AREA_PRIVADA'),

    -- Areas de acceso y almacenamiento
    ('Garaje interno', 'AREA_ACCESO_ALMACENAMIENTO'), ('Garaje externo', 'AREA_ACCESO_ALMACENAMIENTO'),
    ('Antejardín', 'AREA_ACCESO_ALMACENAMIENTO'), ('Hall de entrada', 'AREA_ACCESO_ALMACENAMIENTO'),
    ('Pasillo', 'AREA_ACCESO_ALMACENAMIENTO'), ('Escaleras', 'AREA_ACCESO_ALMACENAMIENTO'),
    ('Depósito', 'AREA_ACCESO_ALMACENAMIENTO'),

    -- Areas exteriores - casas
    ('Patio', 'AREA_EXTERIOR_CASA'), ('Jardín', 'AREA_EXTERIOR_CASA'), ('Terraza', 'AREA_EXTERIOR_CASA'),
    ('Zona BBQ', 'AREA_EXTERIOR_CASA'), ('Kiosco', 'AREA_EXTERIOR_CASA'), ('Patio interior', 'AREA_EXTERIOR_CASA'),
    ('Patio posterior', 'AREA_EXTERIOR_CASA'), ('Balcón', 'AREA_EXTERIOR_CASA'),

    -- Zonas comunes basicas - Conjunto o Edificio
    ('Lobby o recepción', 'ZONA_COMUN_BASICA'), ('Portería', 'ZONA_COMUN_BASICA'),
    ('Pasillos y corredores', 'ZONA_COMUN_BASICA'), ('Escaleras', 'ZONA_COMUN_BASICA'),
    ('Ascensores', 'ZONA_COMUN_BASICA'), ('Hall de acceso', 'ZONA_COMUN_BASICA'),
    ('Vestíbulos', 'ZONA_COMUN_BASICA'), ('Zonas de circulación peatonal', 'ZONA_COMUN_BASICA'),
    ('Cuartos técnicos y de servicios', 'ZONA_COMUN_BASICA'),

    -- Zonas sociales y recreativas
    ('Salón social', 'ZONA_SOCIAL_RECREATIVA'), ('Salón comunal', 'ZONA_SOCIAL_RECREATIVA'),
    ('Piscina para adultos', 'ZONA_SOCIAL_RECREATIVA'), ('Piscina infantil', 'ZONA_SOCIAL_RECREATIVA'),
    ('Jacuzzi', 'ZONA_SOCIAL_RECREATIVA'), ('Sauna', 'ZONA_SOCIAL_RECREATIVA'), ('Turco', 'ZONA_SOCIAL_RECREATIVA'),
    ('Gimnasio', 'ZONA_SOCIAL_RECREATIVA'), ('Parque infantil', 'ZONA_SOCIAL_RECREATIVA'),
    ('Zona BBQ', 'ZONA_SOCIAL_RECREATIVA'), ('Terraza social', 'ZONA_SOCIAL_RECREATIVA'),
    ('Solárium', 'ZONA_SOCIAL_RECREATIVA'), ('Salón de juegos', 'ZONA_SOCIAL_RECREATIVA'),
    ('Cancha deportiva', 'ZONA_SOCIAL_RECREATIVA'), ('Zona verde', 'ZONA_SOCIAL_RECREATIVA'),
    ('Jardines', 'ZONA_SOCIAL_RECREATIVA'),

    -- Zonas vehiculares
    ('Parqueaderos de visitantes', 'ZONA_VEHICULAR'), ('Parqueaderos comunales', 'ZONA_VEHICULAR'),
    ('Vías internas', 'ZONA_VEHICULAR'), ('Zona de circulación vehicular', 'ZONA_VEHICULAR'),
    ('Bicicleteros', 'ZONA_VEHICULAR'), ('Zona para motocicletas', 'ZONA_VEHICULAR'),

    -- Otras zonas comunes
    ('Zona para mascotas', 'OTRA_ZONA_COMUN'), ('Senderos peatonales', 'OTRA_ZONA_COMUN'),
    ('Mirador', 'OTRA_ZONA_COMUN'), ('Salón de reuniones', 'OTRA_ZONA_COMUN'), ('Coworking', 'OTRA_ZONA_COMUN'),
    ('Zona para estudio infantil', 'OTRA_ZONA_COMUN'), ('Depósitos comunes', 'OTRA_ZONA_COMUN'),
    ('Cuarto de basuras', 'OTRA_ZONA_COMUN'), ('Zona de lavandería comunal', 'OTRA_ZONA_COMUN'),
    ('Cuarto de máquinas', 'OTRA_ZONA_COMUN'),

    -- Zonas tecnicas y de servicio
    ('Cuarto de bombas', 'ZONA_TECNICA_SERVICIO'), ('Cuarto de máquinas', 'ZONA_TECNICA_SERVICIO'),
    ('Tanques de almacenamiento de agua', 'ZONA_TECNICA_SERVICIO'), ('Subestación eléctrica', 'ZONA_TECNICA_SERVICIO'),
    ('Cuarto de telecomunicaciones', 'ZONA_TECNICA_SERVICIO');
