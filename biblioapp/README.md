# BiblioApp — Caso Práctico #2

Proyecto base para el Caso Práctico #2 de SC-403. Ver el enunciado completo en `caso_practico_2.md`.

## Qué ya funciona

- Catálogo de libros: `GET /libros` y `GET /libros/{id}`.
- Login personalizado con BCrypt (`/login`), sin restricciones de rol todavía.
- 3 usuarios de ejemplo cargados por `seed-data.sql`.

## Qué te toca construir

Ver la sección "Requisitos obligatorios" de `caso_practico_2.md`. Resumen:

1. Entidad `Prestamo` con `@ManyToOne` a `Libro` y `Usuario`.
2. Enum `Rol` + `@PreAuthorize` restringiendo operaciones de bibliotecario (incluye "un LECTOR ve solo sus propios préstamos" — hay una pista en el enunciado, R3).
3. `LibroRestController` (obligatorio, 3 endpoints) y `PrestamoRestController` con `GET /api/prestamos/atrasados` (obligatorio; `POST /api/prestamos` es bonus chico, no hace falta para el requisito).
4. Consulta JPQL `prestamosAtrasados()` — es la única obligatoria de R5. Los otros dos ejemplos (`caso_practico_2.md`, R5) son opcionales, para estudiar el patrón antes de escribir la tuya.
5. Colección de Postman con las pruebas.

## Cómo arrancar

1. Crear la base de datos:
   ```sql
   CREATE DATABASE IF NOT EXISTS biblioappdb;
   ```
2. Configurar la variable de entorno `DB_PASSWORD` con tu contraseña de MySQL local:
   ```powershell
   setx DB_PASSWORD "tu-password"
   ```
   (abrir una terminal nueva después de esto para que tome el valor)
3. Arrancar la app desde VS Code (botón Run sobre `BiblioappApplication.java`) o con:
   ```
   ./mvnw spring-boot:run
   ```
4. Ejecutar `seed-data.sql` contra `biblioappdb` (con la app ya arrancada al menos una vez, para que Hibernate cree las tablas).
5. Abrir `http://localhost:8080/libros`.

## Credenciales de ejemplo

| Usuario | Password | Rol |
|---|---|---|
| `bibliotecaria1` | `password123` | BIBLIOTECARIO |
| `lector1` | `password123` | LECTOR |
| `lector2` | `password123` | LECTOR |

## Problemas comunes

Ver la tabla "Problemas comunes" en `caso_practico_2.md`.
