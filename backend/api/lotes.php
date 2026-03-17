<?php

require_once __DIR__ . '/db.php';

header('Content-Type: application/json; charset=utf-8');

try {
    $pdo = mysql_connection();
    $stmt = $pdo->query('SELECT id, nombre, url, creado_en FROM lotes ORDER BY nombre ASC');

    echo json_encode([
        'ok' => true,
        'data' => $stmt->fetchAll(),
    ], JSON_UNESCAPED_UNICODE);
} catch (Throwable $e) {
    http_response_code(500);
    echo json_encode([
        'ok' => false,
        'error' => 'Error al obtener lotes',
        'detail' => $e->getMessage(),
    ], JSON_UNESCAPED_UNICODE);
}
