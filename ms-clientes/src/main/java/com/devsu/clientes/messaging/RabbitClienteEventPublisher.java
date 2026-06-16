package com.devsu.clientes.messaging;

import com.devsu.clientes.messaging.event.ClienteEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/** Publicador real sobre RabbitMQ. Inactivo en perfil de test. */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class RabbitClienteEventPublisher implements ClienteEventPublisher {

    private final RabbitTemplate rabbitTemplate;

    @Value("${app.messaging.exchange}")
    private String exchange;

    @Override
    public void publish(ClienteEvent event) {
        String routingKey = "cliente." + event.getTipo().name().toLowerCase();
        rabbitTemplate.convertAndSend(exchange, routingKey, event);
        log.info("Evento publicado [{}] clienteId={}", routingKey, event.getClienteId());
    }
}
