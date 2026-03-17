import { sequelize } from '../src/lib/db';
import '../src/lib/models';

async function main() {
  await sequelize.authenticate();
  console.log('Conexión MySQL OK');
}

main().finally(() => sequelize.close());
