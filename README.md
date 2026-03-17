# Car Collector HW (Android)

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
