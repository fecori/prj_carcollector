CREATE DATABASE IF NOT EXISTS carcollector_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carcollector_db;

CREATE TABLE IF NOT EXISTS lotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL UNIQUE,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS autos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    lote_id INT NOT NULL,
    nombre VARCHAR(255) NOT NULL,
    serie VARCHAR(100),
    anio SMALLINT,
    imagen_url VARCHAR(500),
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_autos_lote
        FOREIGN KEY (lote_id) REFERENCES lotes(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS coleccion_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    auto_id INT NOT NULL,
    estado ENUM('LO_TENGO', 'ME_FALTA') NOT NULL DEFAULT 'ME_FALTA',
    notas VARCHAR(500),
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_coleccion_auto
        FOREIGN KEY (auto_id) REFERENCES autos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE KEY uq_coleccion_auto (auto_id)
);

CREATE INDEX idx_autos_lote_id ON autos(lote_id);
CREATE INDEX idx_autos_nombre ON autos(nombre);
CREATE INDEX idx_coleccion_estado ON coleccion_usuario(estado);
