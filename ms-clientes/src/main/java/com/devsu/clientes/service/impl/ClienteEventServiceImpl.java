package com.devsu.clientes.service.impl;

import com.devsu.clientes.domain.Cliente;
import com.devsu.clientes.messaging.ClienteEventPublisher;
import com.devsu.clientes.messaging.event.ClienteEvent;
import com.devsu.clientes.service.ClienteEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ClienteEventServiceImpl implements ClienteEventService {

    private final ClienteEventPublisher eventPublisher;

    @Override
    public void publicarCreado(Cliente cliente) {
        publicar(ClienteEvent.Tipo.CREATED, cliente);
    }

    @Override
    public void publicarActualizado(Cliente cliente) {
        publicar(ClienteEvent.Tipo.UPDATED, cliente);
    }

    @Override
    public void publicarEliminado(Cliente cliente) {
        publicar(ClienteEvent.Tipo.DELETED, cliente);
    }

    private void publicar(ClienteEvent.Tipo tipo, Cliente cliente) {
        eventPublisher.publish(ClienteEvent.builder()
                .tipo(tipo)
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .estado(cliente.getEstado())
                .build());
    }
}
