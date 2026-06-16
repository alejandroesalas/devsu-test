package com.devsu.it.config;

import org.slf4j.LoggerFactory;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.Network;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.containers.output.Slf4jLogConsumer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.utility.MountableFile;

import java.nio.file.Path;
import java.time.Duration;

public class ContainerConfig {

    public static final Network NETWORK = Network.newNetwork();


    public static final PostgreSQLContainer<?> POSTGRES =
        new PostgreSQLContainer<>("postgres:16-alpine")
            .withNetwork(NETWORK)
            .withNetworkAliases("postgres")
            .withDatabaseName("postgres")
            .withUsername("devsu")
            .withPassword("devsu123")
            .withCopyFileToContainer(
                MountableFile.forClasspathResource("init-db.sql"),
                "/docker-entrypoint-initdb.d/01-init.sql"
            );

    public static final RabbitMQContainer RABBITMQ =
        new RabbitMQContainer("rabbitmq:3.13-management-alpine")
            .withNetwork(NETWORK)
            .withNetworkAliases("rabbitmq");


    public static final GenericContainer<?> MS_CLIENTES =
        new GenericContainer<>(
            new ImageFromDockerfile("ms-clientes-test", false)
                .withDockerfile(serviceDockerfile("ms-clientes"))
        )
        .withNetwork(NETWORK)
        .withNetworkAliases("ms-clientes")
        .withExposedPorts(8081)
        .withEnv("SERVER_PORT", "8081")
        .withEnv("DB_URL", "jdbc:postgresql://postgres:5432/clientes_db")
        .withEnv("DB_USER", "devsu")
        .withEnv("DB_PASSWORD", "devsu123")
        .withEnv("RABBITMQ_HOST", "rabbitmq")
        .withEnv("RABBITMQ_PORT", "5672")
        .withEnv("RABBITMQ_USER", "guest")
        .withEnv("RABBITMQ_PASSWORD", "guest")
        .withEnv("SWAGGER_ENABLED", "false")
        .dependsOn(POSTGRES, RABBITMQ)
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("ms-clientes")))
        .waitingFor(Wait.forHttp("/clientes")
            .forStatusCode(200)
            .withStartupTimeout(Duration.ofMinutes(5)));

    public static final GenericContainer<?> MS_CUENTAS =
        new GenericContainer<>(
            new ImageFromDockerfile("ms-cuentas-test", false)
                .withDockerfile(serviceDockerfile("ms-cuentas"))
        )
        .withNetwork(NETWORK)
        .withNetworkAliases("ms-cuentas")
        .withExposedPorts(8082)
        .withEnv("SERVER_PORT", "8082")
        .withEnv("DB_URL", "jdbc:postgresql://postgres:5432/cuentas_db")
        .withEnv("DB_USER", "devsu")
        .withEnv("DB_PASSWORD", "devsu123")
        .withEnv("RABBITMQ_HOST", "rabbitmq")
        .withEnv("RABBITMQ_PORT", "5672")
        .withEnv("RABBITMQ_USER", "guest")
        .withEnv("RABBITMQ_PASSWORD", "guest")
        .withEnv("SWAGGER_ENABLED", "false")
        .dependsOn(POSTGRES, RABBITMQ)
        .withLogConsumer(new Slf4jLogConsumer(LoggerFactory.getLogger("ms-cuentas")))
        .waitingFor(Wait.forHttp("/cuentas")
            .forStatusCode(200)
            .withStartupTimeout(Duration.ofMinutes(5)));

    static Path serviceDockerfile(String name) {
        Path candidate = Path.of("..", name, "Dockerfile").toAbsolutePath().normalize();
        if (candidate.toFile().exists()) return candidate;
        return Path.of(name, "Dockerfile").toAbsolutePath().normalize();
    }
}
