# SOLUCION — Caso Practico 1

## Endpoints implementados en el desarrollo de mi codigo

- **GET /eventos** — obtiene y muestra la lista completa de todos los eventos cargados en la base de datos.
- **GET /eventos/{id}** — obtiene y muestra el detalle de un evento especifico (mediante el ID del evento), con toda su informacion (fecha, lugar, categoria, precio, etc.).
- **GET /eventos/nuevo** — Abre el formulario vacio sin ningun placeholder para poder crear un nuevo evento en la base de datos
- **POST /eventos** — recibe los datos que se enviaron mediante el formulario de crear evento, valida que esten correctos y cumpla con los filtros y estandares de la base de datos y luego guarda el evento en la base de datos. Si hay errores, muestra los errores mediante alertas en el frontend del formulario explicando el error al intentar realiazar el post, ya sea fecha nombre o algun otro requisito que se haya violado.
- **GET /eventos/{id}/editar** — abre el mismo formulario de crear evento pero esta vez los inputs tienen datos precargados con los datos del evento que se quiere modificar.
- **POST /eventos/{id}** — recibe los datos modificados (los cuales fueron enviados mediante el formulario de edicion), valida que la informacion cumpla con los estandarez de la base de datos y en caso que si actualiza el evento existente en la base de datos.
- **POST /eventos/{id}/eliminar** — borra el evento de la base de datos y muestra la lista nueva (la lista sin el elemento eliminado).
- **GET /eventos/categoria/{categoria}** — filtra los eventos por medio del atributo de categoria (las distintas categorias establecidas, por ejemplo: Musica, Tecnologia, Taller) se puede acceder desde la URL directa o desde el selector de categorias que agregue en el frontend.

## Validaciones en Evento

Eleji las anotaciones pensando en que datos tiene sentido exigir para un evento:

- **@NotBlank** lo use pare verificar el nombre en nombre, no tiene sentido un evento sin nombre, es el campo mas importante y se especifica que el nombre es necesario.
- **@NotNull + @Future** lo use pare verificar la fecha — la fecha es obligatoria, es  necesario que haya una fecha y que sea futura, no se puede anunciar eventos pasados o eventos sin fehca.
- **@Positive** lo use pare verificar el cupoMaximo — el cupo tiene que ser mayor a cero, no tendria sentido un evento sin personas o con una cantidad negativa, no se puede tener una cantidad negativa de invitados.
- **@PositiveOrZero** o use pare verificar el precio — puede ser gratis (0) pero no negativo, negativo implacaria que hay un pago al asistir al evento, puede ser confuso, por ende solo puede ser cero o un numero positivo, no espeficique la divisa del precio para que no hayan confusiones con usuarios extranjeros.
- **@Size** lo use para verificar el nombre, descripcion, lugar, categoria y organizador del evento —  esto se usa para evitar que alguien escriba textos enormes y rompa la base de datos o la vista.

## Modal de confirmacion para eliminar, mitigar errores al borrar eventos indeseados

En vez de usar el confirm() del navegador mejor use un modal de Bootstrap como requiere el proyecto. Cada boton "Eliminar" en las cards de las lista de eventos tiene dos atributos:

- `data-bs-toggle="modal"` — le dice a Bootstrap que este boton abre un modal.
- `data-bs-target="#modalEliminar"` — le indica al boton cual modal debe abrir.

Ademas, use `th:data-id` y `th:data-nombre` para guardar el ID y el nombre del evento en el boton.

Cuando el modal se abre, Bootstrap acciona el evento `show.bs.modal`. Ahi uso JavaScript para leer el `data-id` del boton que acciono el modal y tambien JavaScript cambia la accion del formulario a `/eventos/{id}/eliminar`. Tambien pongo el nombre del evento en el modal para que el usuario sepa cual evento va a borrar y este seguro si es el correcto, esto para evitar borrar eventos indeseados.

## Decisiones tecnicas durante el desarrollo del caso de estudio

- Use **LocalDate** para la fecha del evento, esto porque solo necesito el dia del evento, la hora es innecesaria.
- Agregue los metodos **isLleno()** y **isProximo()** en la entidad Evento para reutilizar la logica en las vistas (mostrar label rojo si esta lleno, label verde si hay cupos).
- El formulario de crear y editar es el mismo (`form.html`). La logica de Thymeleaf decide si el action va a POST /eventos (nuevo evento en la base de datos) o POST /eventos/{id} (editar un evento existente en la base de datos) segun si el evento tiene o no ID.
- Agregue un selector de categorias en el frontend usando un `<select>` de Bootstrap con JavaScript para navegar en el filtro, inicialmente funcionaba unicamente mediante la edicion de la URL pero agregue este select para que sea una funcion utilizable para el usuario.
