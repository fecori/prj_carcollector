import { Sequelize } from 'sequelize';

export const sequelize = new Sequelize(
  process.env.DB_NAME || 'carcollector_db',
  process.env.DB_USER || 'carcollector_user',
  process.env.DB_PASS || 'carcollector_pass',
  {
    host: process.env.DB_HOST || '127.0.0.1',
    port: Number(process.env.DB_PORT || 3306),
    dialect: 'mysql',
    logging: false,
    define: {
      freezeTableName: true,
      timestamps: false,
    },
  }
);
