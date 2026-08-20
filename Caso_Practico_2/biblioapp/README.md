# BiblioApp — Caso Práctico #2

Sistema de préstamo de libros (SC-403). Catálogo, login, préstamos con roles
y API REST.

## Qué incluye

- Catálogo de libros: `GET /libros` y `GET /libros/{id}` (público).
- Login personalizado con BCrypt (`/login`).
- Préstamos: `GET /prestamos`, `GET /prestamos/nuevo`, `POST /prestamos`,
  `POST /prestamos/{id}/devolver` (solo BIBLIOTECARIO; el LECTOR solo ve sus
  propios préstamos).
- Roles `BIBLIOTECARIO` / `LECTOR` (enum `Rol`) con `@PreAuthorize` y página 403.
- API REST:
  - `GET /api/libros` (público), `GET /api/libros/{id}`, `POST /api/libros`
    (BIBLIOTECARIO, `@Valid`, 201), `PUT`/`DELETE` (BIBLIOTECARIO).
  - `GET /api/prestamos/atrasados` (BIBLIOTECARIO) — consulta JPQL
    `prestamosAtrasados()`.
- Sección de préstamos atrasados en rojo en `GET /prestamos`.

## Cómo arrancar

1. Crear la base de datos:
   ```sql
   CREATE DATABASE IF NOT EXISTS biblioappdb;
   ```
2. Configurar la variable de entorno `DB_PASSWORD` con tu contraseña de MySQL.
3. Arrancar la app:
   ```
   mvn spring-boot:run
   ```
4. Ejecutar `seed-data.sql` contra `biblioappdb` (con la app ya arrancada al
   menos una vez, para que Hibernate cree las tablas).
5. Abrir `http://localhost:8080/libros`.

## Credenciales de ejemplo

| Usuario | Password | Rol |
|---|---|---|
| `bibliotecaria1` | `password123` | BIBLIOTECARIO |
| `lector1` | `password123` | LECTOR |
| `lector2` | `password123` | LECTOR |

## Postman

Importar `biblioapp.postman_collection.json`. Login por sesión (cookie) con
token CSRF; la API no necesita CSRF.