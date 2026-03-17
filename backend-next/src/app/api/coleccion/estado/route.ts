import { NextRequest, NextResponse } from 'next/server';
import { readJwt } from '@/lib/auth';
import { sequelize } from '@/lib/db';

export async function POST(request: NextRequest) {
  await sequelize.authenticate();
  const payload = readJwt(request);
  if (!payload) return NextResponse.json({ ok: false, error: 'No autorizado' }, { status: 401 });

  const body = await request.json();
  const autoId = Number(body.auto_id || 0);
  const estado = String(body.estado || 'ME_FALTA');
  const notas = String(body.notas || '').trim();

  if (!autoId || !['LO_TENGO', 'ME_FALTA'].includes(estado)) {
    return NextResponse.json({ ok: false, error: 'Parámetros inválidos' }, { status: 422 });
  }

  await sequelize.query(
    `INSERT INTO coleccion_usuario(usuario_id, auto_id, estado, notas)
     VALUES (:uid, :aid, :estado, :notas)
     ON DUPLICATE KEY UPDATE estado = VALUES(estado), notas = VALUES(notas)`,
    {
      replacements: {
        uid: payload.usuarioId,
        aid: autoId,
        estado,
        notas: notas || null,
      },
    }
  );

  return NextResponse.json({ ok: true });
}
