import { NextRequest, NextResponse } from 'next/server';
import { readJwt } from '@/lib/auth';
import { sequelize } from '@/lib/db';

export async function GET(request: NextRequest) {
  await sequelize.authenticate();
  const payload = readJwt(request);
  if (!payload) return NextResponse.json({ ok: false, error: 'No autorizado' }, { status: 401 });

  const [rows] = await sequelize.query(
    `SELECT a.id, a.nombre, a.serie, a.anio, a.imagen_url, l.nombre AS lote,
            COALESCE(c.estado, 'ME_FALTA') AS estado
     FROM autos a
     INNER JOIN lotes l ON l.id = a.lote_id
     LEFT JOIN coleccion_usuario c ON c.auto_id = a.id AND c.usuario_id = :uid
     ORDER BY l.nombre, a.nombre`,
    { replacements: { uid: payload.usuarioId } }
  );

  return NextResponse.json({ ok: true, data: rows });
}
