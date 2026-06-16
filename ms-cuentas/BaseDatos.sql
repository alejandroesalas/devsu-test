-- cuentas_db — esquema y datos de ejemplo
-- Las tablas se crean automáticamente vía JPA; este script sirve como referencia
-- y para poblar datos manualmente si se desea.
--
-- Si se cargan estos datos, NO ejecutar los casos 2-3 de Postman (se duplicarían).
-- Los casos 4 (movimientos) y 5 (reporte) sí se ejecutan vía API.

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

-- Read-model local del cliente, replicado de forma asíncrona desde ms-clientes
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

-- saldo_disponible = saldo_inicial (estado inicial, sin movimientos)
INSERT INTO cuentas (numero_cuenta, tipo_cuenta, saldo_inicial, saldo_disponible, estado, cliente_id) VALUES
    ('478758', 'AHORROS',   2000.00, 2000.00, TRUE, 'jlema'),
    ('225487', 'CORRIENTE',  100.00,  100.00, TRUE, 'mmontalvo'),
    ('495878', 'AHORROS',      0.00,    0.00, TRUE, 'josorio'),
    ('496825', 'AHORROS',    540.00,  540.00, TRUE, 'mmontalvo'),
    ('585545', 'CORRIENTE', 1000.00, 1000.00, TRUE, 'jlema')
ON CONFLICT (numero_cuenta) DO NOTHING;
