import * as cheerio from 'cheerio';
import { sequelize } from './db';

const BASE_URL = 'https://t-hunted.blogspot.com';
const LOTES_URL = `${BASE_URL}/p/lotes-hot-wheels.html`;

type Lote = { nombre: string; url: string };

type Detail = { descripcion: string | null; imagenes: Array<{ nombre: string; url: string }> };

function absoluteUrl(url: string): string {
  if (url.startsWith('http://') || url.startsWith('https://')) return url;
  if (url.startsWith('/')) return `${BASE_URL}${url}`;
  return `${BASE_URL}/${url}`;
}

async function fetchText(url: string): Promise<string> {
  const res = await fetch(url, { headers: { 'User-Agent': 'CarCollectorNextScraper/1.0' } });
  if (!res.ok) throw new Error(`Error HTTP ${res.status} en ${url}`);
  return await res.text();
}

export async function scrapeLotesIndex(): Promise<Lote[]> {
  const html = await fetchText(LOTES_URL);
  const $ = cheerio.load(html);
  const map = new Map<string, Lote>();

  $('a[href]').each((_, el) => {
    const nombre = $(el).text().trim();
    const href = absoluteUrl($(el).attr('href') || '');
    if (!nombre) return;
    if (!href.includes('t-hunted.blogspot.com')) return;
    if (href.includes('/p/lotes-hot-wheels.html')) return;
    map.set(href, { nombre, url: href });
  });

  return Array.from(map.values()).sort((a, b) => a.nombre.localeCompare(b.nombre));
}

export async function scrapeLoteDetail(url: string): Promise<Detail> {
  const html = await fetchText(url);
  const $ = cheerio.load(html);
  const descripcion = $('meta[name="description"]').attr('content')?.trim() || null;

  const imgs: Array<{ nombre: string; url: string }> = [];
  $('img[src]').each((_, el) => {
    const src = absoluteUrl($(el).attr('src') || '');
    const alt = ($(el).attr('alt') || 'Auto sin nombre').trim();
    if (!src) return;
    if (!src.includes('blogger.googleusercontent.com') && !src.includes('bp.blogspot.com')) return;
    imgs.push({ nombre: alt, url: src });
  });

  const uniq = new Map<string, { nombre: string; url: string }>();
  imgs.forEach((i) => uniq.set(i.url, i));
  return { descripcion, imagenes: Array.from(uniq.values()) };
}

export async function saveLoteToDb(lote: Lote, detail: Detail): Promise<void> {
  await sequelize.query(
    `INSERT INTO lotes(nombre, url, descripcion)
     VALUES(:nombre, :url, :descripcion)
     ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion)`,
    { replacements: { nombre: lote.nombre, url: lote.url, descripcion: detail.descripcion } }
  );

  const [loteRows] = await sequelize.query('SELECT id FROM lotes WHERE url = :url LIMIT 1', {
    replacements: { url: lote.url },
  });
  const loteId = Number((loteRows as any[])[0]?.id);

  await sequelize.query(
    'DELETE ai FROM auto_imagenes ai INNER JOIN autos a ON a.id = ai.auto_id WHERE a.lote_id = :loteId',
    { replacements: { loteId } }
  );
  await sequelize.query('DELETE FROM autos WHERE lote_id = :loteId', { replacements: { loteId } });

  for (let i = 0; i < detail.imagenes.length; i += 1) {
    const img = detail.imagenes[i];
    await sequelize.query(
      'INSERT INTO autos(lote_id, nombre, imagen_url) VALUES(:loteId, :nombre, :imagen)',
      { replacements: { loteId, nombre: img.nombre.slice(0, 255), imagen: img.url } }
    );

    const [autoRows] = await sequelize.query('SELECT LAST_INSERT_ID() AS id');
    const autoId = Number((autoRows as any[])[0]?.id);

    await sequelize.query(
      'INSERT INTO auto_imagenes(auto_id, imagen_url, orden) VALUES(:autoId, :url, :orden)',
      { replacements: { autoId, url: img.url, orden: i } }
    );
  }
}

export async function runScrape(): Promise<{ totalLotes: number }> {
  await sequelize.authenticate();
  const lotes = await scrapeLotesIndex();

  for (const lote of lotes) {
    const detail = await scrapeLoteDetail(lote.url);
    await saveLoteToDb(lote, detail);
  }

  return { totalLotes: lotes.length };
}
