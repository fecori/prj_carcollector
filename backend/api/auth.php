<?php

require_once __DIR__ . '/db.php';

function json_response(array $payload, int $code = 200): void
{
    http_response_code($code);
    header('Content-Type: application/json; charset=utf-8');
    echo json_encode($payload, JSON_UNESCAPED_UNICODE);
    exit;
}

function request_body(): array
{
    $raw = file_get_contents('php://input');
    if (!$raw) return [];
    $data = json_decode($raw, true);
    return is_array($data) ? $data : [];
}

function bearer_token(): ?string
{
    $headers = getallheaders();
    $auth = $headers['Authorization'] ?? $headers['authorization'] ?? '';
    if (preg_match('/Bearer\s+(.*)$/i', $auth, $m)) {
        return trim($m[1]);
    }
    return null;
}

function require_user(PDO $pdo): array
{
    $token = bearer_token();
    if (!$token) {
        json_response(['ok' => false, 'error' => 'No autorizado'], 401);
    }

    $sql = "SELECT u.id, u.nombre, u.email
            FROM sesiones_usuario s
            INNER JOIN usuarios u ON u.id = s.usuario_id
            WHERE s.token = :token AND s.expira_en > NOW()";
    $st = $pdo->prepare($sql);
    $st->execute(['token' => $token]);
    $user = $st->fetch();

    if (!$user) {
        json_response(['ok' => false, 'error' => 'Sesión inválida'], 401);
    }

    return $user;
}
