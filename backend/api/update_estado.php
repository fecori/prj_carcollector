<?php

require_once __DIR__ . '/auth.php';

try {
    $pdo = mysql_connection();
    $usuario = require_user($pdo);
    $body = request_body();

    $autoId = (int)($body['auto_id'] ?? 0);
    $estado = $body['estado'] ?? 'ME_FALTA';
    $notas = trim($body['notas'] ?? '');

    if (!in_array($estado, ['LO_TENGO', 'ME_FALTA'], true) || $autoId <= 0) {
        json_response(['ok' => false, 'error' => 'Parámetros inválidos'], 422);
    }

    $sql = "INSERT INTO coleccion_usuario(usuario_id, auto_id, estado, notas)
            VALUES (:uid, :aid, :estado, :notas)
            ON DUPLICATE KEY UPDATE estado = VALUES(estado), notas = VALUES(notas)";
    $st = $pdo->prepare($sql);
    $st->execute([
        'uid' => $usuario['id'],
        'aid' => $autoId,
        'estado' => $estado,
        'notas' => $notas === '' ? null : $notas,
    ]);

    json_response(['ok' => true]);
} catch (Throwable $e) {
    json_response(['ok' => false, 'error' => 'Error actualizando estado', 'detail' => $e->getMessage()], 500);
}
