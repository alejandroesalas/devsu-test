-- clientes_db — esquema y datos de ejemplo
-- Las tablas se crean automáticamente vía JPA; este script sirve como referencia
-- y para poblar datos manualmente si se desea.
--
-- Si se cargan estos datos, NO ejecutar los casos 1 de Postman (se duplicarían).

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

-- Herencia JOINED: clientes comparte la PK con personas
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

-- La contraseña aquí es texto plano de ejemplo; vía API se almacena cifrada con BCrypt.
INSERT INTO clientes (id, cliente_id, contrasena, estado) VALUES
    (1, 'jlema',     '1234', TRUE),
    (2, 'mmontalvo', '5678', TRUE),
    (3, 'josorio',   '1245', TRUE)
ON CONFLICT (id) DO NOTHING;

SELECT setval(pg_get_serial_sequence('personas','id'), (SELECT MAX(id) FROM personas));
