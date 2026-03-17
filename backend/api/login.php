<?php

require_once __DIR__ . '/auth.php';

try {
    $pdo = mysql_connection();
    $body = request_body();

    $email = mb_strtolower(trim($body['email'] ?? ''));
    $password = $body['password'] ?? '';

    $st = $pdo->prepare('SELECT id, nombre, email, password_hash FROM usuarios WHERE email = :email');
    $st->execute(['email' => $email]);
    $user = $st->fetch();

    if (!$user || !password_verify($password, $user['password_hash'])) {
        json_response(['ok' => false, 'error' => 'Credenciales inválidas'], 401);
    }

    $token = bin2hex(random_bytes(32));
    $expira = (new DateTime('+15 days'))->format('Y-m-d H:i:s');

    $save = $pdo->prepare('INSERT INTO sesiones_usuario(usuario_id, token, expira_en) VALUES (:uid, :token, :expira)');
    $save->execute(['uid' => $user['id'], 'token' => $token, 'expira' => $expira]);

    json_response([
        'ok' => true,
        'token' => $token,
        'usuario' => ['id' => (int)$user['id'], 'nombre' => $user['nombre'], 'email' => $user['email']]
    ]);
} catch (Throwable $e) {
    json_response(['ok' => false, 'error' => 'Error al iniciar sesión', 'detail' => $e->getMessage()], 500);
}
