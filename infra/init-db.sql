-- Crea las bases de datos (patrón database-per-service)
CREATE DATABASE clientes_db;
CREATE DATABASE cuentas_db;

-- =====================================================================
--  clientes_db
-- =====================================================================
\connect clientes_db

CREATE TABLE IF NOT EXISTS personas (
    id              BIGSERIAL PRIMARY KEY,
    nombre          VARCHAR(255) NOT NULL,
    genero          VARCHAR(50)  NOT NULL,
    edad            INTEGER      NOT NULL,
    identificacion  VARCHAR(50)  NOT NULL UNIQUE,
    direccion       VARCHAR(255) NOT NULL,
    telefono        VARCHAR(50)  NOT NULL
);

CREATE TABLE IF NOT EXISTS clientes (
    id          BIGINT PRIMARY KEY REFERENCES personas(id),
    cliente_id  VARCHAR(50)  NOT NULL UNIQUE,
    contrasena  VARCHAR(255) NOT NULL,
    estado      BOOLEAN      NOT NULL
);

INSERT INTO personas (id, nombre, genero, edad, identificacion, direccion, telefono) VALUES
    (1, 'Jose Lema',          'Masculino', 35, '0102030405', 'Otavalo sn y principal', '098254785'),
    (2, 'Marianela Montalvo', 'Femenino',  30, '0102030406', 'Amazonas y NNUU',        '097548965'),
    (3, 'Juan Osorio',        'Masculino', 40, '0102030407', '13 junio y Equinoccial', '098874587')
ON CONFLICT (id) DO NOTHING;

-- Contraseñas en texto plano solo para datos de ejemplo; vía API se almacenan con BCrypt
INSERT INTO clientes (id, cliente_id, contrasena, estado) VALUES
    (1, 'jlema',     '1234', TRUE),
    (2, 'mmontalvo', '5678', TRUE),
    (3, 'josorio',   '1245', TRUE)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('personas','id'), (SELECT MAX(id) FROM personas));

-- =====================================================================
--  cuentas_db
-- =====================================================================
\connect cuentas_db

CREATE TABLE IF NOT EXISTS cuentas (
    id               BIGSERIAL PRIMARY KEY,
    numero_cuenta    VARCHAR(50)    NOT NULL UNIQUE,
    tipo_cuenta      VARCHAR(20)    NOT NULL,
    saldo_inicial    NUMERIC(19,2)  NOT NULL,
    saldo_disponible NUMERIC(19,2)  NOT NULL,
    estado           BOOLEAN        NOT NULL,
    cliente_id       VARCHAR(50)    NOT NULL
);

CREATE TABLE IF NOT EXISTS movimientos (
    id               BIGSERIAL PRIMARY KEY,
    fecha            TIMESTAMP      NOT NULL,
    tipo_movimiento  VARCHAR(20)    NOT NULL,
    valor            NUMERIC(19,2)  NOT NULL,
    saldo            NUMERIC(19,2)  NOT NULL,
    cuenta_id        BIGINT         NOT NULL REFERENCES cuentas(id)
);

CREATE TABLE IF NOT EXISTS cliente_view (
    cliente_id  VARCHAR(50)  PRIMARY KEY,
    nombre      VARCHAR(255) NOT NULL,
    estado      BOOLEAN      NOT NULL
);

INSERT INTO cliente_view (cliente_id, nombre, estado) VALUES
    ('jlema',     'Jose Lema',          TRUE),
    ('mmontalvo', 'Marianela Montalvo', TRUE),
    ('josorio',   'Juan Osorio',        TRUE)
ON CONFLICT (cliente_id) DO NOTHING;

INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
    ('478758', 'AHORROS',   2000.00, 2000.00, TRUE, 'jlema'),
    ('225487', 'CORRIENTE',  100.00,  100.00, TRUE, 'mmontalvo'),
    ('495878', 'AHORROS',      0.00,    0.00, TRUE, 'josorio'),
    ('496825', 'AHORROS',    540.00,  540.00, TRUE, 'mmontalvo'),
    ('585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, 'jlema')
ON CONFLICT (numero_cuenta) DO NOTHING;
