import jwt from 'jsonwebtoken';
import { NextRequest } from 'next/server';

const JWT_SECRET = process.env.JWT_SECRET || 'super_secret_key';

export function createJwt(payload: { usuarioId: number; email: string }): string {
  return jwt.sign(payload, JWT_SECRET, { expiresIn: '15d' });
}

export function readJwt(request: NextRequest): { usuarioId: number; email: string } | null {
  const auth = request.headers.get('authorization') || '';
  const token = auth.startsWith('Bearer ') ? auth.slice(7) : null;
  if (!token) return null;

  try {
    return jwt.verify(token, JWT_SECRET) as { usuarioId: number; email: string };
  } catch {
    return null;
  }
}
