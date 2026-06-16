package com.devsu.clientes.service;

import com.devsu.clientes.dto.ClientePatchRequest;
import com.devsu.clientes.dto.ClienteRequest;
import com.devsu.clientes.dto.ClienteResponse;

import java.util.List;

public interface ClienteService {
    ClienteResponse crear(ClienteRequest request);
    List<ClienteResponse> listar();
    ClienteResponse obtenerPorClienteId(String clienteId);
    ClienteResponse actualizar(String clienteId, ClienteRequest request);
    ClienteResponse actualizarParcial(String clienteId, ClientePatchRequest request);
    void eliminar(String clienteId);
}
