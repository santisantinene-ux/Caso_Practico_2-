# Evidencia — capturas de pantalla

Colocá aquí las 6 capturas requeridas (con estos nombres exactos). No las puedo
generar yo porque requieren la app corriendo contra tu MySQL local; abajo tenés
qué debe mostrar cada una y cómo obtenerla.

| Archivo | Qué debe mostrar | Cómo obtenerla |
|---|---|---|
| `01-catalogo.png` | El catálogo con los 12 libros y su disponibilidad (`x / y disponibles`). | Logueado o no, abrir `http://localhost:8080/libros`. |
| `02-prestamo.png` | El formulario de registrar préstamo (rol bibliotecario). | Login `bibliotecaria1` → menú **Prestamos** → **Registrar prestamo** (`/prestamos/nuevo`). |
| `03-403.png` | Un lector viendo la página de acceso denegado. | Login `lector1` → escribir en la URL `http://localhost:8080/prestamos` → aparece la página 403. |
| `04-postman-login.png` | La request de login en Postman devolviendo **200** con el token JWT (`{ "token": "..." }`). | Postman → request **1. Login BIBLIOTECARIO (JWT)**. |
| `05-postman-api.png` | La respuesta JSON de la API de libros. | Postman → request **2. GET /api/libros**. |
| `06-atrasados.png` | El resultado de la consulta de atrasados. | Postman → request **7. GET /api/prestamos/atrasados** (o la sección roja en `/prestamos`). |

> Sugerencia: para `03-403.png` asegurate de estar logueado como `lector1` (no anónimo),
> así ves la página **403** y no el login — 401 (no autenticado) vs 403 (sin permiso).
