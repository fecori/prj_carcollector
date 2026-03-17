<?php

require_once __DIR__ . '/db.php';

header('Content-Type: application/json; charset=utf-8');

try {
    $pdo = mysql_connection();
    $sql = "SELECT l.id, l.nombre, l.url, l.descripcion, l.creado_en,
                   COUNT(DISTINCT a.id) AS total_autos,
                   COUNT(DISTINCT ai.id) AS total_imagenes
            FROM lotes l
            LEFT JOIN autos a ON a.lote_id = l.id
            LEFT JOIN auto_imagenes ai ON ai.auto_id = a.id
            GROUP BY l.id
            ORDER BY l.nombre ASC";

    $stmt = $pdo->query($sql);

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
