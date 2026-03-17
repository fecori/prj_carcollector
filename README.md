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

## Backend alterno con Next.js + Sequelize + mysql2

Se agregó un backend nuevo en `backend-next/` sin borrar el backend PHP.

Revisa `backend-next/README.md` para instalación, endpoints y scraping.
