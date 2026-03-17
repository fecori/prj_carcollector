# Car Collector HW (Android)

Aplicativo Android para revisar lotes de Hot Wheels y marcar qué autos tienes o te faltan.

## Vistas Android (XML)

En `app/src/main/res/layout`:
- `activity_login.xml`: inicio de sesión.
- `activity_register.xml`: registro de usuarios.
- `activity_main.xml`: listado de lotes con buscador.
- `item_lot.xml`: tarjeta de lote.
- `activity_lot_detail.xml`: detalle con `WebView`.

## Backend MySQL + API

Carpeta `backend/`:
- `db/schema.sql`: tablas completas para usuarios/sesiones/lotes/autos/imágenes/colección.
- `api/register.php`: alta de usuario.
- `api/login.php`: login y creación de token.
- `api/lotes.php`: lotes con conteos.
- `api/mi_coleccion.php`: autos con estado del usuario autenticado.
- `api/update_estado.php`: marcar `LO_TENGO` o `ME_FALTA`.

### Tablas principales

- `usuarios`
- `sesiones_usuario`
- `lotes`
- `autos`
- `auto_imagenes`
- `coleccion_usuario`

## Scraping completo (lotes + imágenes)

Se agregó script:
- `backend/scripts/scrape_t_hunted.php`

Qué hace:
1. Lee `https://t-hunted.blogspot.com/p/lotes-hot-wheels.html`.
2. Obtiene enlaces de todos los lotes.
3. Entra a cada lote y extrae imágenes del contenido.
4. Guarda lote + descripción en `lotes`.
5. Crea registros en `autos` y `auto_imagenes` con la URL de cada imagen.

### Ejecutar scraping

1. Levanta MySQL:
Aplicativo Android para revisar los lotes de Hot Wheels publicados en:

- https://t-hunted.blogspot.com/p/lotes-hot-wheels.html

## Qué hace

- Descarga y lista los lotes detectados en la página de referencia.
- Permite buscar por nombre de lote.
- Al tocar un lote, abre la página en una vista integrada (`WebView`) para revisar imágenes y contenido.

## Dónde están las vistas

Las vistas están en XML dentro de `app/src/main/res/layout`:

- `activity_main.xml`: lista de lotes + buscador.
- `item_lot.xml`: tarjeta individual de cada lote.
- `activity_lot_detail.xml`: `WebView` para el detalle.

La lógica Kotlin está en `app/src/main/java/com/carcollector`.

## Conexión MySQL y tablas

Se agregó una base backend mínima en `backend/` con:

- `backend/db/schema.sql`: creación de base y tablas.
- `backend/api/config.php`: configuración por variables de entorno.
- `backend/api/db.php`: conexión MySQL con `PDO`.
- `backend/api/lotes.php`: endpoint de ejemplo para listar lotes.
- `backend/docker-compose.yml`: MySQL 8 con carga automática de tablas.

### Tablas creadas

- `lotes`: lotes de Hot Wheels (`nombre`, `url`).
- `autos`: autos por lote (`lote_id`, `nombre`, `serie`, `anio`, `imagen_url`).
- `coleccion_usuario`: estado de colección (`LO_TENGO` / `ME_FALTA`) por auto.

### Levantar MySQL local

```bash
cd backend
docker compose up -d
```

2. Ejecuta scraper:
```bash
php backend/scripts/scrape_t_hunted.php
```

## Nota de conexión Android

En `ApiClient.kt` la base está en:
- `http://10.0.2.2:8000/backend/api`

Si usas otro host/puerto, cámbialo allí.

> Nota: la app Android ya no consulta `t-hunted.blogspot.com` directamente; ahora consume únicamente los datos del backend/MySQL por `lotes.php`.
Con eso se crea automáticamente el esquema desde `db/schema.sql`.

## Requisitos

- Android Studio Jellyfish o superior.
- JDK 17.
- SDK de Android 34.

## Ejecutar app Android
## Ejecutar

1. Abre la carpeta en Android Studio.
2. Sincroniza Gradle.
3. Ejecuta en emulador o dispositivo físico.
