<?php

require_once __DIR__ . '/../api/db.php';

const BASE_URL = 'https://t-hunted.blogspot.com';
const LOTES_URL = BASE_URL . '/p/lotes-hot-wheels.html';

function fetch_html(string $url): string
{
    $ch = curl_init($url);
    curl_setopt_array($ch, [
        CURLOPT_RETURNTRANSFER => true,
        CURLOPT_FOLLOWLOCATION => true,
        CURLOPT_TIMEOUT => 30,
        CURLOPT_USERAGENT => 'Mozilla/5.0 (CarCollectorScraper)',
    ]);
    $html = curl_exec($ch);
    if ($html === false) {
        throw new RuntimeException('Error cURL: ' . curl_error($ch));
    }
    curl_close($ch);
    return $html;
}

function dom_xpath(string $html): DOMXPath
{
    $doc = new DOMDocument();
    @$doc->loadHTML($html);
    return new DOMXPath($doc);
}

function absolute_url(string $href): string
{
    if (str_starts_with($href, 'http://') || str_starts_with($href, 'https://')) return $href;
    if (str_starts_with($href, '/')) return BASE_URL . $href;
    return BASE_URL . '/' . ltrim($href, '/');
}

function scrape_lotes_index(): array
{
    $html = fetch_html(LOTES_URL);
    $xp = dom_xpath($html);
    $nodes = $xp->query('//a[@href]');

    $out = [];
    foreach ($nodes as $a) {
        $name = trim($a->textContent ?? '');
        $href = absolute_url($a->getAttribute('href'));
        if ($name === '') continue;
        if (!str_contains($href, 't-hunted.blogspot.com')) continue;
        if (str_contains($href, '/p/lotes-hot-wheels.html')) continue;
        $out[$href] = ['nombre' => $name, 'url' => $href];
    }
    ksort($out);
    return array_values($out);
}

function scrape_lote_detail(string $url): array
{
    $html = fetch_html($url);
    $xp = dom_xpath($html);

    $descNode = $xp->query('//meta[@name="description"]')->item(0);
    $descripcion = $descNode ? trim($descNode->getAttribute('content')) : null;

    $imgNodes = $xp->query('//img[@src]');
    $images = [];
    foreach ($imgNodes as $img) {
        $src = trim($img->getAttribute('src'));
        if ($src === '') continue;
        $src = absolute_url($src);
        $alt = trim($img->getAttribute('alt')) ?: 'Auto sin nombre';
        if (!str_contains($src, 'blogger.googleusercontent.com') && !str_contains($src, 'bp.blogspot.com')) {
            continue;
        }
        $images[] = ['nombre' => $alt, 'url' => $src];
    }

    $uniq = [];
    foreach ($images as $it) {
        $uniq[$it['url']] = $it;
    }

    return [
        'descripcion' => $descripcion,
        'imagenes' => array_values($uniq),
    ];
}

function save_to_db(PDO $pdo, array $lote, array $detail): void
{
    $insLote = $pdo->prepare(
        'INSERT INTO lotes(nombre, url, descripcion) VALUES(:nombre, :url, :descripcion)
         ON DUPLICATE KEY UPDATE nombre = VALUES(nombre), descripcion = VALUES(descripcion)'
    );
    $insLote->execute([
        'nombre' => $lote['nombre'],
        'url' => $lote['url'],
        'descripcion' => $detail['descripcion'],
    ]);

    $findLote = $pdo->prepare('SELECT id FROM lotes WHERE url = :url LIMIT 1');
    $findLote->execute(['url' => $lote['url']]);
    $loteId = (int)$findLote->fetchColumn();

    $insAuto = $pdo->prepare('INSERT INTO autos(lote_id, nombre, imagen_url) VALUES(:lote_id, :nombre, :imagen)');
    $insImg = $pdo->prepare('INSERT INTO auto_imagenes(auto_id, imagen_url, orden) VALUES(:auto_id, :url, :ord)');

    // Limpieza para re-scrape idempotente
    $pdo->prepare('DELETE ai FROM auto_imagenes ai INNER JOIN autos a ON a.id = ai.auto_id WHERE a.lote_id = :lote')
        ->execute(['lote' => $loteId]);
    $pdo->prepare('DELETE FROM autos WHERE lote_id = :lote')->execute(['lote' => $loteId]);

    foreach ($detail['imagenes'] as $i => $img) {
        $insAuto->execute([
            'lote_id' => $loteId,
            'nombre' => mb_substr($img['nombre'], 0, 255),
            'imagen' => $img['url'],
        ]);
        $autoId = (int)$pdo->lastInsertId();
        $insImg->execute([
            'auto_id' => $autoId,
            'url' => $img['url'],
            'ord' => $i,
        ]);
    }
}

$pdo = mysql_connection();
$pdo->beginTransaction();

try {
    $lotes = scrape_lotes_index();
    echo "Lotes encontrados: " . count($lotes) . PHP_EOL;

    foreach ($lotes as $index => $lote) {
        echo '[' . ($index + 1) . '/' . count($lotes) . '] ' . $lote['nombre'] . PHP_EOL;
        $detail = scrape_lote_detail($lote['url']);
        save_to_db($pdo, $lote, $detail);
        usleep(250000);
    }

    $pdo->commit();
    echo "Scraping finalizado." . PHP_EOL;
} catch (Throwable $e) {
    $pdo->rollBack();
    fwrite(STDERR, 'Error scraping: ' . $e->getMessage() . PHP_EOL);
    exit(1);
}
