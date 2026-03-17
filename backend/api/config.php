<?php

return [
    'db_host' => getenv('DB_HOST') ?: '127.0.0.1',
    'db_port' => getenv('DB_PORT') ?: '3306',
    'db_name' => getenv('DB_NAME') ?: 'carcollector_db',
    'db_user' => getenv('DB_USER') ?: 'carcollector_user',
    'db_pass' => getenv('DB_PASS') ?: 'carcollector_pass',
];
