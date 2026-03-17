# Backend Next.js (nuevo, adicional al backend PHP)

Este backend **no reemplaza** al backend PHP existente. Es una alternativa nueva usando:

- Next.js (App Router)
- Sequelize
- mysql2

## Requisitos

- Node.js 20+
- MySQL con el mismo esquema usado por `backend/db/schema.sql`

## Configuración

1. Copia variables de entorno:

```bash
cp .env.example .env.local
```

2. Instala dependencias:

```bash
npm install
```

3. Verifica conexión:

```bash
npm run db:sync
```

## Ejecutar

```bash
npm run dev
```

Servidor en `http://localhost:3001`.

## Endpoints

- `POST /api/auth/register`
- `POST /api/auth/login`
- `GET /api/lotes`
- `GET /api/coleccion` (Bearer token)
- `POST /api/coleccion/estado` (Bearer token)
- `POST /api/scrape` (ejecuta scraping y guarda en MySQL)

## Scraping por script

También puedes ejecutar scraping desde consola:

```bash
npm run scrape
```
