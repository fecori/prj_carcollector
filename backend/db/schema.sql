CREATE DATABASE IF NOT EXISTS carcollector_db CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE carcollector_db;

CREATE TABLE IF NOT EXISTS usuarios (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(120) NOT NULL,
    email VARCHAR(190) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS sesiones_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    token VARCHAR(128) NOT NULL UNIQUE,
    expira_en DATETIME NOT NULL,
    creado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_sesion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS lotes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(255) NOT NULL,
    url VARCHAR(500) NOT NULL UNIQUE,
    descripcion TEXT,
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
        ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS auto_imagenes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    auto_id INT NOT NULL,
    imagen_url VARCHAR(500) NOT NULL,
    orden INT DEFAULT 0,
    CONSTRAINT fk_auto_imagenes_auto
        FOREIGN KEY (auto_id) REFERENCES autos(id)
        ON DELETE CASCADE ON UPDATE CASCADE
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS coleccion_usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    usuario_id INT NOT NULL,
    auto_id INT NOT NULL,
    estado ENUM('LO_TENGO', 'ME_FALTA') NOT NULL DEFAULT 'ME_FALTA',
    notas VARCHAR(500),
    actualizado_en TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    CONSTRAINT fk_coleccion_usuario
        FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_coleccion_auto
        FOREIGN KEY (auto_id) REFERENCES autos(id)
        ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY uq_usuario_auto (usuario_id, auto_id)
    CONSTRAINT fk_coleccion_auto
        FOREIGN KEY (auto_id) REFERENCES autos(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    UNIQUE KEY uq_coleccion_auto (auto_id)
);

CREATE INDEX idx_autos_lote_id ON autos(lote_id);
CREATE INDEX idx_autos_nombre ON autos(nombre);
CREATE INDEX idx_coleccion_estado ON coleccion_usuario(estado);
CREATE INDEX idx_sesion_usuario ON sesiones_usuario(usuario_id);
