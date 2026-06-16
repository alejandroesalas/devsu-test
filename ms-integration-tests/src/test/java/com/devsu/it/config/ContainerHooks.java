package com.devsu.it.config;

import io.cucumber.java.AfterAll;
import io.cucumber.java.BeforeAll;
import org.testcontainers.lifecycle.Startables;

public class ContainerHooks {

    @BeforeAll
    public static void startContainers() {
        Startables.deepStart(
            ContainerConfig.MS_CLIENTES,
            ContainerConfig.MS_CUENTAS
        ).join();
    }

    @AfterAll
    public static void stopContainers() {
        ContainerConfig.MS_CUENTAS.stop();
        ContainerConfig.MS_CLIENTES.stop();
        ContainerConfig.RABBITMQ.stop();
        ContainerConfig.POSTGRES.stop();
    }
}
