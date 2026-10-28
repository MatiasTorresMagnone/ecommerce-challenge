INSERT INTO producto (nombre, descripcion, precio, imagen, activo)
SELECT v.nombre, v.descripcion, v.precio, NULL, v.activo
FROM (VALUES
    ('Teclado mecánico',     'Teclado RGB con conexión USB y switches azules',     49.90, TRUE),
    ('Mouse inalámbrico',    'Mouse ergonómico de alta precisión, batería de 12 meses', 24.50, TRUE),
    ('Auriculares Bluetooth','Cancelación de ruido y 30 horas de batería',          79.00, TRUE),
    ('Monitor 24 pulgadas',  'Full HD, panel IPS y 75 Hz',                         159.99, TRUE),
    ('Webcam HD',            'Cámara 1080p con micrófono integrado',                35.00, TRUE),
    ('Mochila para notebook','Resistente al agua, entra hasta 15,6 pulgadas',       29.90, TRUE),
    ('Parlante portátil',    'Sonido estéreo, resistente a salpicaduras',           39.90, TRUE),
    ('Cable HDMI 2 metros',  'Producto descontinuado (sirve para probar el estado inactivo)', 12.00, FALSE)
) AS v(nombre, descripcion, precio, activo)
WHERE NOT EXISTS (
    SELECT 1 FROM producto p WHERE LOWER(p.nombre) = LOWER(v.nombre)
);