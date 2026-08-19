-- BiblioApp - datos de ejemplo
-- Ejecutar DESPUES de arrancar la app al menos una vez (para que Hibernate cree las tablas)

INSERT INTO usuarios (username, password, nombre_completo, email, rol) VALUES
-- password real para los 3: "password123" (hash BCrypt)
('bibliotecaria1', '$2a$10$SUCY1zxmbiOhX7yNV1Wl8Om8sLKzuHUfCS9kniQ9lWKyFRDJD1dza', 'Ana Bibliotecaria', 'ana.biblio@example.com', 'BIBLIOTECARIO'),
('lector1', '$2a$10$SUCY1zxmbiOhX7yNV1Wl8Om8sLKzuHUfCS9kniQ9lWKyFRDJD1dza', 'Luis Lector', 'luis.lector@example.com', 'LECTOR'),
('lector2', '$2a$10$SUCY1zxmbiOhX7yNV1Wl8Om8sLKzuHUfCS9kniQ9lWKyFRDJD1dza', 'Maria Lectora', 'maria.lectora@example.com', 'LECTOR');

INSERT INTO libros (titulo, autor, isbn, categoria, copias_totales, copias_disponibles) VALUES
('Cien anios de soledad', 'Gabriel Garcia Marquez', '978-0307350438', 'Ficcion', 3, 3),
('Clean Code', 'Robert C. Martin', '978-0132350884', 'Tecnico', 5, 5),
('El principito', 'Antoine de Saint-Exupery', '978-0156012195', 'Infantil', 4, 4),
('Effective Java', 'Joshua Bloch', '978-0134685991', 'Tecnico', 3, 3),
('1984', 'George Orwell', '978-0451524935', 'Ficcion', 4, 4),
('Sapiens', 'Yuval Noah Harari', '978-0062316097', 'Ensayo', 2, 2),
('Design Patterns', 'Erich Gamma et al.', '978-0201633610', 'Tecnico', 2, 2),
('La sombra del viento', 'Carlos Ruiz Zafon', '978-8408043645', 'Ficcion', 3, 3),
('Introduction to Algorithms', 'Cormen, Leiserson, Rivest, Stein', '978-0262033848', 'Tecnico', 2, 2),
('Rayuela', 'Julio Cortazar', '978-8437604572', 'Ficcion', 2, 2),
('Spring in Action', 'Craig Walls', '978-1617294945', 'Tecnico', 3, 3),
('Breve historia del tiempo', 'Stephen Hawking', '978-0553380163', 'Ensayo', 2, 2);

-- Prestamos de ejemplo
-- IMPORTANTE: tambien bajamos copias_disponibles de los libros prestados sin devolver

-- 1) Atrasado: fecha_limite en el pasado y sin devolver
INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_limite, fecha_devolucion) VALUES
((SELECT id FROM libros WHERE isbn = '978-0132350884'),        -- Clean Code
 (SELECT id FROM usuarios WHERE username = 'lector1'),
 DATE_SUB(CURDATE(), INTERVAL 30 DAY), DATE_SUB(CURDATE(), INTERVAL 16 DAY), NULL);

-- 2) Vigente: aun dentro del plazo, sin devolver.
INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_limite, fecha_devolucion) VALUES
((SELECT id FROM libros WHERE isbn = '978-0451524935'),        -- 1984
 (SELECT id FROM usuarios WHERE username = 'lector2'),
 DATE_SUB(CURDATE(), INTERVAL 3 DAY), DATE_ADD(CURDATE(), INTERVAL 11 DAY), NULL);

-- 3) Ya devuelto: sirve para probar el estado "Devuelto".
INSERT INTO prestamos (libro_id, usuario_id, fecha_prestamo, fecha_limite, fecha_devolucion) VALUES
((SELECT id FROM libros WHERE isbn = '978-0307350438'),        -- Cien anios de soledad
 (SELECT id FROM usuarios WHERE username = 'lector1'),
 DATE_SUB(CURDATE(), INTERVAL 40 DAY), DATE_SUB(CURDATE(), INTERVAL 26 DAY),
 DATE_SUB(CURDATE(), INTERVAL 28 DAY));

-- Bajamos copias de los prestamos que no fueron devueltos (1 y 2).
UPDATE libros SET copias_disponibles = copias_disponibles - 1 WHERE isbn = '978-0132350884'; -- Clean Code
UPDATE libros SET copias_disponibles = copias_disponibles - 1 WHERE isbn = '978-0451524935'; -- 1984
