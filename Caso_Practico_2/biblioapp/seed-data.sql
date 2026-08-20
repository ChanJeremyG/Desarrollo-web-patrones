-- BiblioApp - datos de ejemplo
-- Ejecutar DESPUES de arrancar la app al menos una vez (para que Hibernate cree las tablas)

INSERT INTO usuarios (username, password, nombre_completo, email, rol) VALUES
-- password real para los 3: "password123" (hash BCrypt)
('bibliotecaria1', '$2a$10$S7S3rMdxgmgM3ym8F6dmROXfKd9n/MxIePqTMZFV5sGIcFkpxt.s6', 'Ana Bibliotecaria', 'ana.biblio@example.com', 'BIBLIOTECARIO'),
('lector1', '$2a$10$S7S3rMdxgmgM3ym8F6dmROXfKd9n/MxIePqTMZFV5sGIcFkpxt.s6', 'Luis Lector', 'luis.lector@example.com', 'LECTOR'),
('lector2', '$2a$10$S7S3rMdxgmgM3ym8F6dmROXfKd9n/MxIePqTMZFV5sGIcFkpxt.s6', 'Maria Lectora', 'maria.lectora@example.com', 'LECTOR');

-- copias_disponibles ya descontadas en los libros con prestamos ACTIVOS (1, 2 y 3)
INSERT INTO libros (titulo, autor, isbn, categoria, copias_totales, copias_disponibles) VALUES
('Cien anios de soledad', 'Gabriel Garcia Marquez', '978-0307350438', 'Ficcion', 3, 2),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Tecnico', 5, 4),
('El principito', 'Antoine de Saint-Exupery', '978-0156012195', 'Infantil', 4, 3),
('Effective Java', 'Joshua Bloch', '978-0134685991', 'Tecnico', 3, 3),
('1984', 'George Orwell', '978-0451524935', 'Ficcion', 4, 4),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 'Ensayo', 2, 2),
('Design Patterns', 'Erich Gamma et al.', '978-0201633610', 'Tecnico', 2, 2),
('La sombra del viento', 'Carlos Ruiz Zafon', '978-8408043645', 'Ficcion', 3, 3),
('Introduction to Algorithms', 'Cormen, Leiserson, Rivest, Stein', '978-0262033848', 'Tecnico', 2, 2),
('Rayuela', 'Julio Cortazar', '978-8437604572', 'Ficcion', 2, 2),
('Spring in Action', 'Craig Walls', '978-1617294945', 'Tecnico', 3, 3),
('Breve historia del tiempo', 'Stephen Hawking', '978-0553380163', 'Ensayo', 2, 2);

-- REQUISITO 2 + 5: prestamos de ejemplo. Dos ATASADOS (fecha_limite en el
-- pasado y fecha_devolucion NULL), uno vigente y uno ya devuelto.
-- IMPORTANTE: ajusta las fechas a tu fecha actual si es necesario, para que
-- al menos un prestamo quede con fecha_limite anterior a HOY.
INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_limite, fecha_devolucion) VALUES
(1, 2, '2026-07-28', '2026-08-11', NULL),  -- ATASADO
(2, 3, '2026-08-01', '2026-08-15', NULL),  -- ATASADO
(3, 2, '2026-08-15', '2026-08-29', NULL),  -- vigente
(4, 3, '2026-07-10', '2026-07-24', '2026-07-20'); -- devuelto a tiempo