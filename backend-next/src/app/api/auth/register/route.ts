import bcrypt from 'bcryptjs';
import { NextRequest, NextResponse } from 'next/server';
import { sequelize } from '@/lib/db';
import { Usuario } from '@/lib/models';

export async function POST(request: NextRequest) {
  await sequelize.authenticate();
  const body = await request.json();
  const nombre = String(body.nombre || '').trim();
  const email = String(body.email || '').trim().toLowerCase();
  const password = String(body.password || '');

  if (!nombre || !email || password.length < 6) {
    return NextResponse.json({ ok: false, error: 'Datos inválidos' }, { status: 422 });
  }

  const exists = await Usuario.findOne({ where: { email } });
  if (exists) {
    return NextResponse.json({ ok: false, error: 'El email ya existe' }, { status: 409 });
  }

  const hash = await bcrypt.hash(password, 10);
  await Usuario.create({ nombre, email, password_hash: hash });

  return NextResponse.json({ ok: true, message: 'Usuario creado' });
}
