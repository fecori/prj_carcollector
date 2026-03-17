import { NextResponse } from 'next/server';
import { runScrape } from '@/lib/scraper';

export async function POST() {
  try {
    const result = await runScrape();
    return NextResponse.json({ ok: true, ...result });
  } catch (error) {
    return NextResponse.json(
      { ok: false, error: 'Error en scraping', detail: (error as Error).message },
      { status: 500 }
    );
  }
}
