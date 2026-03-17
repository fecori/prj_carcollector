import bcrypt from 'bcryptjs';
import { NextRequest, NextResponse } from 'next/server';
import { sequelize } from '@/lib/db';
import { createJwt } from '@/lib/auth';
import { SesionUsuario, Usuario } from '@/lib/models';

export async function POST(request: NextRequest) {
  await sequelize.authenticate();
  const body = await request.json();
  const email = String(body.email || '').trim().toLowerCase();
  const password = String(body.password || '');

  const usuario = await Usuario.findOne({ where: { email } });
  if (!usuario) return NextResponse.json({ ok: false, error: 'Credenciales inválidas' }, { status: 401 });

  const valid = await bcrypt.compare(password, String(usuario.get('password_hash')));
  if (!valid) return NextResponse.json({ ok: false, error: 'Credenciales inválidas' }, { status: 401 });

  const token = createJwt({ usuarioId: Number(usuario.get('id')), email });
  const expira = new Date(Date.now() + 15 * 24 * 60 * 60 * 1000);
  await SesionUsuario.create({ usuario_id: usuario.get('id'), token, expira_en: expira });

  return NextResponse.json({
    ok: true,
    token,
    usuario: {
      id: usuario.get('id'),
      nombre: usuario.get('nombre'),
      email: usuario.get('email'),
    },
  });
}
