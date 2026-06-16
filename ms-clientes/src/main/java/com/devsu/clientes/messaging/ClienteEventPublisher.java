package com.devsu.clientes.messaging;

import com.devsu.clientes.messaging.event.ClienteEvent;

public interface ClienteEventPublisher {
    void publish(ClienteEvent event);
}
