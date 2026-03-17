<?php

require_once __DIR__ . '/auth.php';

try {
    $pdo = mysql_connection();
    $body = request_body();

    $nombre = trim($body['nombre'] ?? '');
    $email = mb_strtolower(trim($body['email'] ?? ''));
    $password = $body['password'] ?? '';

    if ($nombre === '' || $email === '' || strlen($password) < 6) {
        json_response(['ok' => false, 'error' => 'Datos inválidos (password mínimo 6)'], 422);
    }

    $hash = password_hash($password, PASSWORD_DEFAULT);
    $st = $pdo->prepare('INSERT INTO usuarios(nombre, email, password_hash) VALUES (:nombre, :email, :hash)');
    $st->execute(['nombre' => $nombre, 'email' => $email, 'hash' => $hash]);

    json_response(['ok' => true, 'message' => 'Usuario creado']);
} catch (PDOException $e) {
    if ((int)$e->getCode() === 23000) {
        json_response(['ok' => false, 'error' => 'El email ya existe'], 409);
    }
    json_response(['ok' => false, 'error' => 'Error al registrar', 'detail' => $e->getMessage()], 500);
}
