package com.devsu.clientes.service.impl;

import com.devsu.clientes.domain.Cliente;
import com.devsu.clientes.dto.ClientePatchRequest;
import com.devsu.clientes.dto.ClienteRequest;
import com.devsu.clientes.dto.ClienteResponse;
import com.devsu.clientes.exception.DuplicateResourceException;
import com.devsu.clientes.exception.ResourceNotFoundException;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.service.ClienteEventService;
import com.devsu.clientes.service.ClienteService;
import com.devsu.clientes.service.mapper.ClienteMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {

    private final ClienteRepository repository;
    private final ClienteMapper mapper;
    private final PasswordEncoder passwordEncoder;
    private final ClienteEventService clienteEventService;

    @Override
    @Transactional
    public ClienteResponse crear(ClienteRequest request) {
        if (repository.existsByClienteId(request.getClienteId())) {
            throw new DuplicateResourceException("Ya existe un cliente con clienteId: " + request.getClienteId());
        }
        if (repository.existsByIdentificacion(request.getIdentificacion())) {
            throw new DuplicateResourceException("Ya existe una persona con identificación: " + request.getIdentificacion());
        }
        Cliente cliente = Cliente.builder()
                .nombre(request.getNombre())
                .genero(request.getGenero())
                .edad(request.getEdad())
                .identificacion(request.getIdentificacion())
                .direccion(request.getDireccion())
                .telefono(request.getTelefono())
                .clienteId(request.getClienteId())
                .contrasena(passwordEncoder.encode(request.getContrasena()))
                .estado(request.getEstado())
                .build();
        Cliente guardado = repository.save(cliente);
        clienteEventService.publicarCreado(guardado);
        return mapper.toResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ClienteResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ClienteResponse obtenerPorClienteId(String clienteId) {
        return mapper.toResponse(buscar(clienteId));
    }

    @Override
    @Transactional
    public ClienteResponse actualizar(String clienteId, ClienteRequest request) {
        Cliente cliente = buscar(clienteId);
        cliente.setNombre(request.getNombre());
        cliente.setGenero(request.getGenero());
        cliente.setEdad(request.getEdad());
        cliente.setIdentificacion(request.getIdentificacion());
        cliente.setDireccion(request.getDireccion());
        cliente.setTelefono(request.getTelefono());
        cliente.setContrasena(passwordEncoder.encode(request.getContrasena()));
        cliente.setEstado(request.getEstado());
        Cliente actualizado = repository.save(cliente);
        clienteEventService.publicarActualizado(actualizado);
        return mapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public ClienteResponse actualizarParcial(String clienteId, ClientePatchRequest request) {
        Cliente cliente = buscar(clienteId);
        if (request.getNombre() != null) cliente.setNombre(request.getNombre());
        if (request.getGenero() != null) cliente.setGenero(request.getGenero());
        if (request.getEdad() != null) cliente.setEdad(request.getEdad());
        if (request.getDireccion() != null) cliente.setDireccion(request.getDireccion());
        if (request.getTelefono() != null) cliente.setTelefono(request.getTelefono());
        if (request.getContrasena() != null) cliente.setContrasena(passwordEncoder.encode(request.getContrasena()));
        if (request.getEstado() != null) cliente.setEstado(request.getEstado());
        Cliente actualizado = repository.save(cliente);
        clienteEventService.publicarActualizado(actualizado);
        return mapper.toResponse(actualizado);
    }

    @Override
    @Transactional
    public void eliminar(String clienteId) {
        Cliente cliente = buscar(clienteId);
        repository.delete(cliente);
        clienteEventService.publicarEliminado(cliente);
    }

    private Cliente buscar(String clienteId) {
        return repository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));
    }
}
