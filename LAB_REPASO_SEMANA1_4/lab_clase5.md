# Laboratorio de Clase 5 — Repaso Integrador

**Curso:** SC-403 Desarrollo de Aplicaciones Web y Patrones
**Tema:** Repaso de las Clases 1 a 4 con TiendaApp
**Duración estimada:** 90 minutos
**Modalidad:** trabajo individual guiado

---

## Objetivo

Tomar el proyecto **TiendaApp** ya construido por el profesor, copiarlo dentro del repositorio del curso que ya tenés en GitHub, levantarlo localmente, comprender cómo está organizado siguiendo el patrón MVC con cuatro capas y aplicar un cambio pequeño usando un workflow Git sencillo.

## Lo que vas a repasar

- **Clase 1:** Git básico — `pull`, `add`, `commit`, `push` y `git log`.
- **Clase 2:** Spring Boot arrancando con Maven Wrapper, `application.properties` y DevTools.
- **Clase 3:** Vistas con Thymeleaf (`th:text`, `th:if`, `th:each`) y componentes de Bootstrap.
- **Clase 4:** Entity con JPA, JpaRepository con query methods, Service con `@Service` y Controller siguiendo MVC.

---

## Antes de empezar

Verificá que tenés todo instalado:

```bash
java --version    # OpenJDK 21
git --version     # 2.x
mysql --version   # 8.x (o tener Workbench abierto)
```

Abrí:

- **VS Code** sin proyecto.
- **MySQL Workbench** con tu conexión local.
- **Navegador** para probar las URLs.
- **GitHub** en una pestaña, con sesión iniciada.

**Material entregado por el profesor:**

- `tiendaapp.zip` — proyecto Spring Boot ya construido.
- `seed-data.sql` — datos de ejemplo para cargar en MySQL.

---

# Parte 1 — Copiar el proyecto dentro de tu repositorio del curso (10 minutos)

Vas a colocar TiendaApp dentro del mismo repositorio donde guardás todos los ejemplos semana a semana. No vas a crear un repo nuevo.

## 1.1 — Actualizar tu repositorio local

Posicionate en la carpeta de tu repositorio del curso y traete los últimos cambios desde GitHub:

```bash
cd ruta\a\tu\repositorio-del-curso
git pull
```

## 1.2 — Crear la carpeta de esta semana y descomprimir el proyecto

Dentro de tu repositorio, crea la carpeta de la semana (usá el nombre que ya venís manejando, por ejemplo `Clase_5` o `Semana_5`):

```bash
mkdir Clase_5
```

Descomprimí `tiendaapp.zip` dentro de esa carpeta. La estructura debería quedar así:

```
tu-repositorio-del-curso/
├── Clase_1/
├── Clase_2/
├── Clase_3/
├── Clase_4/
└── Clase_5/
    └── tiendaapp/
        ├── pom.xml
        ├── src/
        ├── seed-data.sql
        └── ...
```

## 1.3 — Abrir el proyecto en VS Code

```bash
cd Clase_5\tiendaapp
code .
```

Esperá a que VS Code descargue las dependencias de Maven la primera vez (puede tardar un par de minutos).

**Checkpoint:** ves la estructura del proyecto en el panel izquierdo de VS Code y no aparecen errores rojos en los archivos `.java`.

---

# Parte 2 — Recorrido por la arquitectura MVC (10 minutos)

Antes de modificar nada, navegá la estructura del proyecto en VS Code:

```
src/main/java/com/ufide/tiendaapp/
├── TiendaappApplication.java
├── entity/Producto.java
├── repository/ProductoRepository.java
├── service/ProductoService.java
└── controller/
    ├── HomeController.java
    └── ProductoController.java
```

Observá los siguientes detalles en cada archivo:

### Entity — `Producto.java`

- `@Entity` y `@Table(name = "productos")` convierten la clase en una tabla.
- `@Id` junto con `@GeneratedValue(strategy = GenerationType.IDENTITY)` define la clave primaria autoincremental.
- `@Column(nullable = false)` agrega restricciones a nivel de columna.
- El constructor vacío `public Producto() {}` es **obligatorio** para JPA.

### Repository — `ProductoRepository.java`

- Extiende `JpaRepository<Producto, Long>`. Esto trae implementados los métodos `findAll`, `findById`, `save`, `deleteById` y `count`.
- Los **query methods** se generan automáticamente leyendo el nombre del método:
  - `findByCategoria(String c)`
  - `findByNombreContainingIgnoreCase(String n)`
  - `findByStockLessThan(int max)`

### Service — `ProductoService.java`

- `@Service` lo registra como bean gestionado por Spring.
- `@Autowired` recibe el repository por inyección de dependencias.
- Encapsula la lógica entre el controlador y la persistencia.

### Controller — `ProductoController.java`

- `@Controller` y `@RequestMapping("/productos")` definen el prefijo común de todas las rutas.
- Llama al `service`, **nunca** al `repository` directamente.
- Devuelve el nombre de la vista Thymeleaf que se renderiza.

### Flujo de una request

```
HTTP -> Controller -> Service -> Repository -> Base de datos
                                                    |
                                                    v
Navegador <- Vista <- Controller <- Service <- Repository
```

---

# Parte 3 — Configurar MySQL y la contraseña de forma segura (20 minutos)

Vamos a configurar dos capas de seguridad para la contraseña:

1. **Variable de entorno persistente (sticky)** — la forma recomendada.
2. **Archivo local ignorado por Git** — un respaldo por si alguien escribe la contraseña en texto.

Si funciona la variable de entorno, te ahorrás escribir la contraseña cada vez que abrís una terminal. Y si por error alguien la escribe directo en un archivo, ese archivo no se sube al repositorio.

## 3.1 — Crear la base de datos

En MySQL Workbench ejecutá:

```sql
CREATE DATABASE tiendadb
  CHARACTER SET utf8mb4
  COLLATE utf8mb4_unicode_ci;
```

## 3.2 — Definir la variable de entorno persistente (sticky)

En Windows, el comando `setx` guarda la variable de entorno de forma permanente para tu usuario. Aparece en cualquier terminal nueva que abras después.

Abrí **PowerShell** (no como administrador) y ejecutá:

```powershell
setx DB_PASSWORD "tu_password_de_mysql"
```

**Importante:**

- `setx` guarda la variable para el usuario actual de forma persistente.
- El cambio **no afecta a la terminal actual**, solo a las que abras después.
- Cerrá la terminal y abrí una nueva para que tome efecto.

Verificá que quedó bien guardada en la terminal nueva:

```powershell
echo $env:DB_PASSWORD
```

Debe imprimir tu password.

> Si estás en Linux o Mac, agregá esta línea al final de tu `~/.bashrc` o `~/.zshrc`:
> ```bash
> export DB_PASSWORD="tu_password_de_mysql"
> ```
> Y reabrí la terminal o ejecutá `source ~/.bashrc`.

## 3.3 — Configurar el archivo local ignorado por Git (respaldo seguro)

El proyecto ya está preparado para leer la contraseña desde la variable de entorno. Pero como respaldo, vamos a crear un archivo de configuración local que **nunca se sube al repositorio**, por si alguien necesita escribir la contraseña en texto.

### Verificar el `.gitignore`

Abrí el archivo `.gitignore` del proyecto y confirmá que en la sección final ya tiene:

```
# Secrets - NUNCA subir
application-local.properties
*.env
```

Estas líneas ya vienen en el proyecto. Si por alguna razón no están, agregalas y guardá.

### Crear `application-local.properties`

En `src/main/resources/` crea un archivo nuevo llamado `application-local.properties` con este contenido:

```properties
# Archivo LOCAL - no se sube al repo (esta en .gitignore)
# Solo se usa si la variable de entorno DB_PASSWORD no esta definida
spring.datasource.password=tu_password_de_mysql
```

### Verificar que Git lo ignora

```bash
git status
```

El archivo `application-local.properties` **no debe aparecer** en la lista de archivos modificados. Si aparece, revisá el `.gitignore` y volvelo a guardar.

## 3.4 — Revisar `application.properties`

Abrí `src/main/resources/application.properties` y verificá que la línea de la contraseña use la sintaxis con variable de entorno:

```properties
spring.datasource.url=${DB_URL:jdbc:mysql://localhost:3306/tiendadb}
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:cambiame_local}
```

La sintaxis `${DB_PASSWORD:cambiame_local}` significa: si existe la variable de entorno `DB_PASSWORD`, usá ese valor; si no, usá el texto `cambiame_local`.

Y al final del archivo, agregá esta línea para que Spring cargue también `application-local.properties` cuando exista:

```properties
spring.profiles.include=local
```

## 3.5 — Correr la aplicación

```bash
.\mvnw.cmd spring-boot:run     # Windows
./mvnw spring-boot:run         # Linux o Mac
```

Esperá a ver en consola: `Started TiendaappApplication in X seconds`.

Hibernate crea automáticamente la tabla `productos` porque la propiedad `spring.jpa.hibernate.ddl-auto=update` está activa.

## 3.6 — Cargar el seed de datos

En Workbench abrí el archivo `seed-data.sql` que viene en la raíz del proyecto y ejecutalo. Debe insertar 8 productos.

**Checkpoint:** ingresá a http://localhost:8080/productos y deberías ver el catálogo con los 8 productos en un grid responsive de Bootstrap.

---

# Parte 4 — Cambio en la vista (20 minutos)

Vas a agregar dos cosas en la página de productos: un badge con la categoría de cada producto y avisos visuales para productos con poco stock o agotados.

## 4.1 — Editar `templates/productos.html`

Abrí el archivo `src/main/resources/templates/productos.html` y, dentro del bloque que dibuja cada card de producto (donde se muestra el nombre y el precio), agregá los siguientes badges:

```html
<span class="badge bg-info mb-2" th:text="${producto.categoria}">categoria</span>

<span th:if="${producto.bajoStock}"
      class="badge bg-warning text-dark">
  Pocas unidades
</span>

<span th:if="${producto.agotado}"
      class="badge bg-danger">
  Agotado
</span>
```

Los métodos `bajoStock` y `agotado` ya están implementados como `isBajoStock()` e `isAgotado()` en `Producto.java`. Thymeleaf los expone como propiedades cuando se llaman desde la vista.

## 4.2 — Verificar el cambio

Si Spring DevTools está activo, basta con guardar y refrescar el navegador. Si no, reiniciá con `.\mvnw.cmd spring-boot:run`.

Cada card debe mostrar ahora la categoría arriba del nombre y, según sus datos, una alerta de stock bajo o de agotado.

**Checkpoint visual:** al menos uno de los 8 productos del seed debe mostrar la alerta amarilla `Pocas unidades` o la roja `Agotado`.

---

# Parte 5 — Probar el catálogo completo (10 minutos)

Con la aplicación corriendo, recorré cada una de las rutas disponibles y prestá atención a qué método del Controller atiende cada una y qué vista responde.

| URL | Método del Controller | Vista renderizada |
|-----|----------------------|-------------------|
| http://localhost:8080/ | `HomeController.home()` | `home.html` |
| http://localhost:8080/productos | `ProductoController.listar()` | `productos.html` |
| http://localhost:8080/productos/1 | `ProductoController.detalle(1)` | `producto.html` |
| http://localhost:8080/productos/categoria/Bebidas | `ProductoController.porCategoria()` | `productos.html` |
| http://localhost:8080/productos/buscar?q=cafe | `ProductoController.buscar()` | `productos.html` |
| http://localhost:8080/productos/bajo-stock | `ProductoController.bajoStock()` | `productos.html` |

## Mini reto

Probá buscar `cafe` o `CAFE`. Devuelve los mismos resultados sin importar mayúsculas o minúsculas. Esto funciona porque en el Repository está definido:

```java
List<Producto> findByNombreContainingIgnoreCase(String nombre);
```

Spring Data JPA tradujo automáticamente ese nombre en un `WHERE LOWER(nombre) LIKE LOWER('%cafe%')` sin que tengas que escribir SQL.

---

# Parte 6 — Subir el resultado a tu repositorio (15 minutos)

Vas a aplicar el workflow Git simple: traer cambios, agregar, commitear y subir.

## 6.1 — Detener la aplicación

En la terminal donde corre Spring Boot, presioná `Ctrl+C`.

## 6.2 — Volver a la raíz del repositorio

Desde la carpeta del proyecto Spring Boot, subí hasta la raíz de tu repositorio del curso:

```bash
cd ..\..\          # ajustá según tu profundidad
```

Deberías quedar en la carpeta que contiene `Clase_1`, `Clase_2`, ..., `Clase_5`.

## 6.3 — Actualizar antes de commitear

Siempre traer los cambios del remoto antes de subir los tuyos:

```bash
git pull
```

## 6.4 — Revisar qué vas a subir

```bash
git status
```

Debés ver el contenido de la carpeta `Clase_5/tiendaapp/` listado como "untracked" o "modified".

**Verificá que `application-local.properties` NO aparezca en la lista.** Si aparece, no continúes hasta que el `.gitignore` lo ignore.

## 6.5 — Agregar, commitear y subir

```bash
git add .
git commit -m "feat(clase5): repaso integrador con TiendaApp y badges de categoria"
git push
```

## 6.6 — Verificar en GitHub

Abrí tu repositorio en GitHub y confirmá:

- La carpeta `Clase_5/tiendaapp/` está visible.
- El último commit es el que acabás de hacer.
- El archivo `application-local.properties` **no aparece** en el repositorio.

## 6.7 — Revisar el historial

En la terminal:

```bash
git log --oneline
```

Vas a ver el commit que acabás de hacer en la parte superior, junto con los commits anteriores de las semanas previas.

---

# Checklist final

Marcá lo que lograste completar durante la clase:

- [ ] El proyecto TiendaApp corre en `http://localhost:8080`.
- [ ] La base de datos `tiendadb` existe en MySQL y tiene 8 productos.
- [ ] La variable de entorno `DB_PASSWORD` está configurada de forma persistente.
- [ ] El archivo `application-local.properties` existe localmente pero NO está en GitHub.
- [ ] La vista muestra badges de categoría y alertas de stock.
- [ ] La carpeta `Clase_5/tiendaapp/` está subida en tu repositorio del curso.
- [ ] Comprendés el flujo Controller → Service → Repository → BD.

---

# Conceptos repasados

| Clase | Conceptos aplicados |
|-------|---------------------|
| Clase 1 | Git básico: `pull`, `add`, `commit`, `push`, `git log`. |
| Clase 2 | Maven Wrapper, `application.properties`, variables de entorno, DevTools, perfiles de Spring. |
| Clase 3 | Thymeleaf (`th:text`, `th:if`, `th:each`), componentes Bootstrap (badges, grid, cards). |
| Clase 4 | JPA con `@Entity`, JpaRepository con query methods, Service con inyección de dependencias, Controller siguiendo MVC. |

---

# Problemas comunes

| Síntoma | Solución |
|---------|----------|
| `Access denied for user 'root'` al arrancar | Cerrá y abrí una terminal nueva para que tome la `DB_PASSWORD`. Verificá con `echo $env:DB_PASSWORD`. |
| `Unknown database 'tiendadb'` | Crear la base de datos con el script SQL de la Parte 3.1. |
| `application-local.properties` aparece al hacer `git status` | Revisá que el `.gitignore` tenga la línea correcta y volvé a hacer `git status`. |
| Whitelabel Error Page en `/productos` | Revisá que el método `listar()` del Controller retorne el nombre correcto de la vista (`"productos"`, sin `.html`). |
| Los badges no aparecen | Asegurate de haber guardado `productos.html` y refrescar el navegador. Si no usa DevTools, reiniciá la aplicación. |
| `git push` falla con "rejected" | Antes de pushear hay que hacer `git pull`. Si igual falla, revisá que estés en la rama correcta con `git branch`. |
| `setx` no parece haber funcionado | Cerrá **completamente** la terminal y abrí una nueva. `setx` no afecta la terminal donde lo ejecutaste. |

---

# Tarea (sin nota, solo para aprender)

Esta tarea no se califica. Su único propósito es que termines el lab por tu cuenta y refuerces los conceptos.

1. Si no llegaste a terminar todas las partes durante la clase, completá lo que falte.
2. Asegurate de que la carpeta `Clase_5/tiendaapp/` esté subida en tu repositorio del curso.
3. Verificá que el último commit corresponda al trabajo de esta semana.

Si te queda tiempo y querés profundizar, podés intentar lo siguiente como práctica extra:

- Agregar un nuevo método al `ProductoService` que liste solo los productos con stock mayor a 10.
- Crear una ruta nueva en el Controller para esa lista (por ejemplo `/productos/disponibles`).
- Mostrar esa lista en una vista propia, reutilizando el grid de Bootstrap.

---

# Recursos de referencia

| Tema | Enlace |
|------|--------|
| Spring Data JPA | https://docs.spring.io/spring-data/jpa/reference/ |
| Thymeleaf | https://www.thymeleaf.org/documentation.html |
| Bootstrap 5 | https://getbootstrap.com/docs/5.3 |
| Git book (en español) | https://git-scm.com/book/es/v2 |
| Variables de entorno en Windows (setx) | https://learn.microsoft.com/en-us/windows-server/administration/windows-commands/setx |
