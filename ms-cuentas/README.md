# ms-cuentas

Microservicio de cuentas, movimientos y reportes. Puerto **8082**, base de datos `cuentas_db`.

Consume eventos de clientes desde RabbitMQ (`cuentas.cliente.queue`) para mantener una réplica local y no depender sincrónicamente de `ms-clientes`.

## Levantar

Desde la raíz del proyecto:

```bash
docker compose up --build
```

> Para el flujo completo hay que levantar ambos servicios, ya que `ms-cuentas` necesita recibir el evento de creación del cliente antes de poder asociarle una cuenta.

## Endpoints

```
POST   /cuentas
GET    /cuentas
GET    /cuentas/{numeroCuenta}
PUT    /cuentas/{numeroCuenta}
PATCH  /cuentas/{numeroCuenta}

POST   /movimientos
GET    /movimientos
GET    /movimientos/{id}

GET    /reportes?clienteId={id}&fechaInicio=yyyy-MM-dd&fechaFin=yyyy-MM-dd
```

Swagger: http://localhost:8082/swagger-ui.html

## Pruebas

```bash
mvn test
```

Corre con H2 en memoria (sin Docker ni RabbitMQ).

## Base de datos

El esquema y datos de ejemplo se encuentran en [`infra/init-db.sql`](../infra/init-db.sql) en la raíz del proyecto.  
Al levantar Docker, Postgres ejecuta ese script automáticamente — las tablas y datos quedan listos sin intervención manual.
