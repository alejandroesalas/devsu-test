package com.devsu.clientes.service;

import com.devsu.clientes.domain.Cliente;

public interface ClienteEventService {
    void publicarCreado(Cliente cliente);
    void publicarActualizado(Cliente cliente);
    void publicarEliminado(Cliente cliente);
}
