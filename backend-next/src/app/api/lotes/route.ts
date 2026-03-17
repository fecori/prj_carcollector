import { NextResponse } from 'next/server';
import { sequelize } from '@/lib/db';

export async function GET() {
  await sequelize.authenticate();

  const [rows] = await sequelize.query(`
    SELECT l.id, l.nombre, l.url, l.descripcion,
           COUNT(DISTINCT a.id) AS total_autos,
           COUNT(DISTINCT ai.id) AS total_imagenes
    FROM lotes l
    LEFT JOIN autos a ON a.lote_id = l.id
    LEFT JOIN auto_imagenes ai ON ai.auto_id = a.id
    GROUP BY l.id
    ORDER BY l.nombre ASC
  `);

  return NextResponse.json({ ok: true, data: rows });
}
