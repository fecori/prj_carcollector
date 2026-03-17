<?php

require_once __DIR__ . '/auth.php';

try {
    $pdo = mysql_connection();
    $usuario = require_user($pdo);

    $sql = "SELECT a.id, a.nombre, a.serie, a.anio, a.imagen_url, l.nombre AS lote,
                   COALESCE(c.estado, 'ME_FALTA') AS estado
            FROM autos a
            INNER JOIN lotes l ON l.id = a.lote_id
            LEFT JOIN coleccion_usuario c
                ON c.auto_id = a.id AND c.usuario_id = :uid
            ORDER BY l.nombre, a.nombre";
    $st = $pdo->prepare($sql);
    $st->execute(['uid' => $usuario['id']]);

    json_response(['ok' => true, 'usuario' => $usuario, 'data' => $st->fetchAll()]);
} catch (Throwable $e) {
    json_response(['ok' => false, 'error' => 'Error en colección', 'detail' => $e->getMessage()], 500);
}
