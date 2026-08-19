# SOLUCION.md — BiblioApp (Caso Practico 2)

## 1. Como arme la entidad Prestamo y su relacion con Libro y Usuario

Lo primero que hice fue pensar en como se relacionan las cosas en una bibloteca real. Un prestamo siempre es de **un solo libro** y de **un solo usuario**, pero un libro puede tener muchos prestamos a lo largo del tiempo y un usuario puede pedir varios libros. Entonces hice la entidad Prestamo con dos relaciones `@ManyToOne`: una hacia Libro y otra hacia Usuario. Las llaves forneas (`libro_id` y `usuario_id`) quedan en la tabla `prestamos`, que es donde tienen sentido.

Puse ambas relaciones como `LAZY` porque normalmente no necesito traer todo el libro y el usuario cada vez que listo prestamos. El problema es que cuando intento mostrar el titulo del libro en la vista con `prestamo.getLibro().getTitulo()`, Hibernate ya cerro la sesion y me tira `LazyInitializationException`. Para resolver eso uso `JOIN FETCH` en las consultas que alimentan las vistas, asi traigo el libro y el usuario en la misma query y no tengo problemas.

Para las fechas use `LocalDate`: `fechaPrestamo` (cuando se presto), `fechaLimite` (14 dias despues) y `fechaDevolucion` (cuando se devolvio). La ultima queda en `NULL` mientras no se devuelva el libro, y esa condicion es la que despues uso para saber si un prestamo sigue abierto o ya no.

## 2. Los roles y por que puse cada @PreAuthorize

El enum `Rol` tiene dos valores: `BIBLIOTECARIO` y `LECTOR`. Active la seguridad por metodo con `@EnableMethodSecurity` en el `SecurityConfig`, porque sin eso las anotaciones `@PreAuthorize` no hacen nada y cualquiera entra a cualquier parte.

Restringi a solo BIBLIOTECARIO estas operaciones:

- **Registrar un prestamo y marcar una devolucion**: tiene sentido porque prestar y recibir libros es trabajo del personal de la bibloteca, no algo que un lector comun haga.
- **Ver el listado de TODOS los prestamos**: ahi aparecen los prestamos de todos los usuarios, no es info que un lector deba ver.
- **Crear un libro desde la API** (`POST /api/libros`): modificar el catalogo es funcion administrativa.

Para el lector hice lo contrario: la pantalla "Mis prestamos" no pide un rol especifico, pero internamente busco el usuario que esta logueado con `Authentication` y solo listo **sus propios** prestamos. Asi un lector nunca ve los de otra persona.

Para la seguridad separe la app en **dos cadenas** (`SecurityFilterChain`), porque la web y la API funcionan distinto. La cadena de la **web** usa login por formulario con sesion y CSRF, y cuando un usuario sin permiso entra a una ruta de bibliotecario lo mando a la pagina HTML `/403`. La cadena de la **API** (`/api/**`) es stateless y se autentica por JWT, sin sesion ni CSRF; ahi una request sin permiso no redirige a ningun lado, devuelve el codigo directo: **401** si no hay token valido y **403** si el usuario esta logueado pero no tiene el rol.

Las separe porque al principio tenia todo en una sola cadena con el login por formulario y el JWT juntos, y eso hacia que una denegacion en la API terminara redirigida al login en vez de devolver 403, que para un cliente REST como Postman esta mal.

## 3. La consulta JPQL de prestamos atrasados

La idea es sencilla: un prestamo esta atrasado cuando se cumplen **dos condiciones** al mismo tiempo. Primero, que no se devolvio (la `fechaDevolucion` es `NULL`). Segundo, que la `fechaLimite` ya paso, o sea, que sea anterior a la fecha de hoy. Para comparar con la fecha actual uso `CURRENT_DATE`, que es como el `LocalDate.now()` pero en JPQL.

La consulta entonces busca todos los Prestamo donde `fechaDevolucion IS NULL AND fechaLimite < CURRENT_DATE`. Le puse `JOIN FETCH` sobre libro y usuario por el mismo motivo que explique antes, y ordene por `fechaLimite` ascendente para que el que lleva mas tiempo atrasado salga primero.

Esta consulta la conecte a dos lugares: al endpoint `GET /api/prestamos/atrasados` para que devuelva el JSON, y a una seccion en la vista de prestamos del bibliotecario donde aparece resaltada en rojo con el libro, el usuario y la fecha limite vencida.

## 4. Endpoints de la API y que codigos de estado devuelven

Para `/api/libros`:

- `GET /api/libros` devuelve **200** con el catalogo completo en JSON. Es publico, no necesita login.
- `GET /api/libros/{id}` devuelve **200** si el libro existe y **404** si no. Uso `ResponseEntity` para controlar el codigo manualmente.
- `POST /api/libros` devuelve **201 Created** si todo va bien. Si el JSON no cumple las validaciones devuelve **400**. Si lo llama un lector devuelve **403**, y si no hay nadie logueado **401**.

Para `/api/prestamos`:

- `GET /api/prestamos/atrasados` devuelve **200** con la lista de prestamos atrasados. Solo puede acceder el BIBLIOTECARIO porque son datos de todos los usuarios.

Para `/api/auth`:

- `POST /api/auth/login` recibe usuario y password, y si son correctos devuelve **200** con el token JWT. Si las credenciales estan mal devuelve **401**.

## 5. Otras cosas que tuve en cuenta

La fecha limite la calculo en `PrestamoService` sumando 14 dias a la fecha de prestamo. Use una constante `DIAS_PLAZO = 14` para no tener el numero magico suelto por el codigo. Cuando se registra un prestamo se descuenta una copia del libro, y cuando se devuelve se suma de vuelta. Ambas operaciones van con `@Transactional` para que si una falla, las dos fallen juntas y no quede el libro desactualizado.

Para el bonus de JWT, el login de la API (`POST /api/auth/login`) valida las credenciales con el mismo `AuthenticationManager` que usa la web y devuelve un token firmado con HS256. Un filtro JWT (`JwtAuthFilter`) lee el header `Authorization: Bearer` en cada request de la API y arma el `Authentication` sin depender de sesiones. Los mismos `@PreAuthorize` funcionan tanto por sesion web como por token JWT.

Los secretos (`DB_PASSWORD` y `JWT_SECRET`) no estan en el codigo, se leen de variables de entorno. La app ni arranca si falta alguno, a proposito, porque el enunciado penaliza si se suben las contrasenas al repo.

La contrasena del usuario la marco con `@JsonIgnore` para que nunca aparezca en los JSON de la API, ya que el Usuario se serializa cuando se devuelven los prestamos.
