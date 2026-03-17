import { sequelize } from '../src/lib/db';
import { runScrape } from '../src/lib/scraper';

async function main() {
  const result = await runScrape();
  console.log(`Scraping completado. Lotes: ${result.totalLotes}`);
}

main()
  .catch((e) => {
    console.error(e);
    process.exit(1);
  })
  .finally(() => sequelize.close());
