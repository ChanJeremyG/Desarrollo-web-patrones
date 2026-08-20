# SOLUCION.md — BiblioApp (Caso Práctico 2)

## 1. La relación `Prestamo → Libro` y por qué la modelé así

Un préstamo siempre pertenece a un solo libro y a un solo usuario, pero un
libro puede prestarse muchas veces a lo largo del tiempo. Por eso la relación
va del lado de `Prestamo`: dos campos `@ManyToOne`, uno hacia `Libro` y otro
hacia `Usuario`. En la base de datos eso se traduce en las columnas
`libro_id` y `usuario_id` dentro de la tabla `prestamos`, que guardan la clave
primaria del libro y del usuario respectivamente. Es exactamente el mismo
patrón que vimos en la Semana 9 con `Curso` y `Profesor`, así que no inventé
nada nuevo: la entidad "muchos" (el préstamo) es la que carga las llaves
foráneas, y el lado "uno" (libro/usuario) no necesita ninguna anotación extra.

Como la relación es `LAZY` (igual que en clase), las consultas de listado usan
`JOIN FETCH` para traer libro y usuario junto con el préstamo en una sola
consulta SQL. Sin eso, mostrar `prestamo.getLibro().getTitulo()` en la tabla
dispararía una consulta extra por cada préstamo (el problema N+1 que vimos en
la Semana 9).

## 2. Qué hace cada `@PreAuthorize` y por qué esa regla

Usé el enum `Rol` (BIBLIOTECARIO, LECTOR) como constante en vez de escribir
los strings a mano. El campo `rol` de `Usuario` pasó a ser de tipo `Rol` con
`@Enumerated(EnumType.STRING)`, de modo que en la base se sigue guardando el
texto "BIBLIOTECARIO"/"LECTOR" pero el código Java nunca escribe esos strings:
compara contra `Rol.BIBLIOTECARIO.name()` o `Rol.LECTOR.name()`.

- En `LibroRestController` (crear, actualizar y eliminar): `hasRole('BIBLIOTECARIO')`.
  Solo quien administra la biblioteca puede crear/editar/borrar del catálogo.
  Elegí esta regla porque son operaciones de escritura sobre un recurso
  compartido: un lector no tiene por qué modificar el catálogo.
- En `PrestamoController` (registrar y devolver): `hasRole('BIBLIOTECARIO')`.
  El préstamo cambia el inventario (`copiasDisponibles`), así que es una
  función del personal de la biblioteca, no de quien lee.
- En el listado `/prestamos`: `hasRole('BIBLIOTECARIO') or hasRole('LECTOR')`
  para que cualquiera logueado pueda ver sus préstamos, y dentro del método
  separo la lógica: si el rol es BIBLIOTECARIO listo todos los préstamos (y la
  sección de atrasados); si es LECTOR, busco su `Usuario` por el username de la
  sesión (`auth.getName()`) y llamo a `findByUsuario`. Esto cumple "un LECTOR
  solo ve sus propios préstamos" sin permitirle ver los ajenos.
- En `PrestamoRestController` (`/api/prestamos/atrasados`): `hasRole('BIBLIOTECARIO')`.
  Los atrasados son un subconjunto del listado completo de préstamos, que
  también está restringido al bibliotecario.

Para que `@PreAuthorize` funcione tuve que agregar `@EnableMethodSecurity` en
`SecurityConfig` (sin eso, las anotaciones se ignoran en silencio). Además, si
un usuario autenticado sin el rol intenta la operación, Spring Security lanza
la excepción de acceso denegado y la configuro con `accessDeniedPage("/403")`
para mostrar una página propia en vez del error blanco.

## 3. La consulta JPQL de préstamos atrasados (Requisito 5.3)

La definición del problema me dio las dos condiciones: atrasado = no se ha
devuelto **y** ya pasó la fecha límite. La primera condición es directa en
JPQL: `p.fechaDevolucion IS NULL`. Para la segunda comparé la fecha límite con
la fecha de hoy usando la función de JPQL `CURRENT_DATE`, que devuelve la
fecha actual del servidor: `p.fechaLimite < CURRENT_DATE`. Juntando ambas con
`AND` obtengo exactamente los préstamos que están pendientes de devolución y
cuya fecha límite ya venció. A la consulta le agregué `JOIN FETCH` de libro y
usuario por el mismo motivo del punto 1: la API la serializa a JSON y la vista
la muestra en HTML, así que necesito los datos relacionados ya cargados para
no encontrarme con un `LazyInitializationException`. La query quedó en
`PrestamoRepository.prestamosAtrasados()` y se usa desde el servicio para
alimentar tanto `GET /api/prestamos/atrasados` como la sección en rojo del
dashboard de préstamos.

Para poder probarla de verdad, en el `seed-data.sql` cargué dos préstamos con
`fecha_limite` en el pasado y `fecha_devolucion` NULL (atrasados), uno vigente
y uno devuelto. Si al ejecutar no devuelve nada, hay que ajustar esas fechas
para que queden antes de la fecha actual.

## 4. Endpoints de la API y códigos de estado

**LibroRestController** (`/api/libros`):
- `GET /api/libros` → catálogo en JSON, **público** (no pide login) → `200`.
- `GET /api/libros/{id}` → `200` con el libro, o `404` si no existe (uso
  `ResponseEntity`).
- `POST /api/libros` → solo BIBLIOTECARIO, valida el body con `@Valid
  @RequestBody` → `201 Created` + header `Location`; si el JSON no pasa las
  validaciones, `@Valid` hace que Spring devuelva `400 Bad Request`.
- `PUT /api/libros/{id}` y `DELETE /api/libros/{id}` → también solo
  BIBLIOTECARIO → `200`/`204`, o `404` si el id no existe.

**PrestamoRestController** (`/api/prestamos`):
- `GET /api/prestamos/atrasados` → solo BIBLIOTECARIO, devuelve la lista de la
  consulta `prestamosAtrasados()` → `200` con el JSON; si el usuario no tiene
  el rol, `403`.

Para la API desactivé CSRF solo en `/api/**` (las rutas HTML siguen
protegidas), igual que en el proyecto de ejemplo de la Semana 12, porque desde
Postman no se manda token CSRF. También puse `@JsonIgnore` sobre el password
de `Usuario` para que la API nunca exponga el hash en el JSON.

## 5. Decisiones técnicas adicionales

- **Fecha límite:** se calcula en `PrestamoService.registrar()` como
  `fechaPrestamo.plusDays(14)`. Por eso el formulario no pide la fecha límite:
  el sistema la calcula sola y la guarda. Por la misma razón le quité la
  validación `@NotNull` a `fechaLimite` en la entidad, ya que el form nunca la
  envía y validarla habría hecho fallar siempre el registro.
- **Inventario:** al registrar se descuenta una unidad de `copiasDisponibles`
  del libro y, si no hay copias, se rechaza el préstamo con un mensaje. Al
  devolver se suma de vuelta. Lo puse en `LibroService` como `descontarCopia` /
  `devolverCopia`.
- **Página 403:** el endpoint `/403` acepta cualquier método HTTP (no solo
  GET) porque el acceso denegado de un `POST`/`PUT` redirige internamente a
  `/403` con el método original; si solo fuera GET, esos casos daban 405.
- **Credenciales:** el `seed-data.sql` original traía un hash BCrypt que no
  correspondía a la contraseña "password123", así que regeneré los hashes
  reales con `BCryptPasswordEncoder` para que el login funcione con las
  credenciales del README.