package com.devsu.clientes.messaging;

import com.devsu.clientes.messaging.event.ClienteEvent;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Implementación vacía para pruebas (sin RabbitMQ). */
@Component
@Profile("test")
public class NoOpClienteEventPublisher implements ClienteEventPublisher {
    @Override
    public void publish(ClienteEvent event) {
        // no-op en pruebas
    }
}
