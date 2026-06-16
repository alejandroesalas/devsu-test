# ms-clientes

Microservicio de clientes. Puerto **8081**, base de datos `clientes_db`.

## Levantar

Desde la raíz del proyecto:

```bash
docker-compose up --build ms-clientes
```

O standalone (requiere Postgres y RabbitMQ corriendo):

```bash
mvn spring-boot:run
```

## Endpoints

```
POST   /clientes
GET    /clientes
GET    /clientes/{clienteId}
PUT    /clientes/{clienteId}
PATCH  /clientes/{clienteId}
DELETE /clientes/{clienteId}
```

Swagger: http://localhost:8081/swagger-ui.html

## Pruebas

```bash
mvn test
```

Corre con H2 en memoria (sin Docker ni RabbitMQ).

## Base de datos

El esquema y datos de ejemplo están en [`BaseDatos.sql`](BaseDatos.sql).  
En ejecución las tablas las crea JPA automáticamente.
