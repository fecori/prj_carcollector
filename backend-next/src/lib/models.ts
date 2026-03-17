import { DataTypes, Model } from 'sequelize';
import { sequelize } from './db';

export class Usuario extends Model {}
Usuario.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    nombre: { type: DataTypes.STRING(120), allowNull: false },
    email: { type: DataTypes.STRING(190), allowNull: false, unique: true },
    password_hash: { type: DataTypes.STRING(255), allowNull: false },
  },
  { sequelize, tableName: 'usuarios' }
);

export class SesionUsuario extends Model {}
SesionUsuario.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    usuario_id: { type: DataTypes.INTEGER, allowNull: false },
    token: { type: DataTypes.STRING(128), allowNull: false, unique: true },
    expira_en: { type: DataTypes.DATE, allowNull: false },
  },
  { sequelize, tableName: 'sesiones_usuario' }
);

export class Lote extends Model {}
Lote.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    nombre: { type: DataTypes.STRING(255), allowNull: false },
    url: { type: DataTypes.STRING(500), allowNull: false, unique: true },
    descripcion: { type: DataTypes.TEXT, allowNull: true },
  },
  { sequelize, tableName: 'lotes' }
);

export class Auto extends Model {}
Auto.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    lote_id: { type: DataTypes.INTEGER, allowNull: false },
    nombre: { type: DataTypes.STRING(255), allowNull: false },
    serie: { type: DataTypes.STRING(100), allowNull: true },
    anio: { type: DataTypes.SMALLINT, allowNull: true },
    imagen_url: { type: DataTypes.STRING(500), allowNull: true },
  },
  { sequelize, tableName: 'autos' }
);

export class AutoImagen extends Model {}
AutoImagen.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    auto_id: { type: DataTypes.INTEGER, allowNull: false },
    imagen_url: { type: DataTypes.STRING(500), allowNull: false },
    orden: { type: DataTypes.INTEGER, allowNull: true },
  },
  { sequelize, tableName: 'auto_imagenes' }
);

export class ColeccionUsuario extends Model {}
ColeccionUsuario.init(
  {
    id: { type: DataTypes.INTEGER, autoIncrement: true, primaryKey: true },
    usuario_id: { type: DataTypes.INTEGER, allowNull: false },
    auto_id: { type: DataTypes.INTEGER, allowNull: false },
    estado: { type: DataTypes.ENUM('LO_TENGO', 'ME_FALTA'), allowNull: false },
    notas: { type: DataTypes.STRING(500), allowNull: true },
  },
  { sequelize, tableName: 'coleccion_usuario' }
);
