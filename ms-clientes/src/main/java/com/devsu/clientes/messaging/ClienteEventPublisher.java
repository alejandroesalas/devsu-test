package com.devsu.clientes.messaging;

import com.devsu.clientes.messaging.event.ClienteEvent;

/** Abstracción para publicar eventos de Cliente */
public interface ClienteEventPublisher {
    void publish(ClienteEvent event);
}
