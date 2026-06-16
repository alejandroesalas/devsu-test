package com.devsu.clientes.service.mapper;

import com.devsu.clientes.domain.Cliente;
import com.devsu.clientes.dto.ClienteResponse;
import org.springframework.stereotype.Component;

/** Mapeo manual entidad <-> DTO (sin exponer la contraseña). */
@Component
public class ClienteMapper {

    public ClienteResponse toResponse(Cliente cliente) {
        return ClienteResponse.builder()
                .id(cliente.getId())
                .clienteId(cliente.getClienteId())
                .nombre(cliente.getNombre())
                .genero(cliente.getGenero())
                .edad(cliente.getEdad())
                .identificacion(cliente.getIdentificacion())
                .direccion(cliente.getDireccion())
                .telefono(cliente.getTelefono())
                .estado(cliente.getEstado())
                .build();
    }
}
