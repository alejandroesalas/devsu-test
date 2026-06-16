package com.devsu.cuentas.messaging;

import com.devsu.cuentas.domain.ClienteView;
import com.devsu.cuentas.messaging.event.ClienteEvent;
import com.devsu.cuentas.repository.ClienteViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

/**
 * Escucha los eventos de Cliente y mantiene actualizado el read-model local
 * (ClienteView). Es la comunicación ASÍNCRONA entre los dos microservicios.
 */
@Slf4j
@Component
@Profile("!test")
@RequiredArgsConstructor
public class ClienteEventConsumer {

    private final ClienteViewRepository clienteViewRepository;

    @RabbitListener(queues = "${app.messaging.queue}")
    public void onClienteEvent(ClienteEvent event) {
        log.info("Evento recibido [{}] clienteId={}", event.getTipo(), event.getClienteId());
        if (event.getTipo() == ClienteEvent.Tipo.DELETED) {
            clienteViewRepository.deleteById(event.getClienteId());
            return;
        }
        ClienteView view = ClienteView.builder()
                .clienteId(event.getClienteId())
                .nombre(event.getNombre())
                .estado(event.getEstado())
                .build();
        clienteViewRepository.save(view);
    }
}
