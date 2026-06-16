package com.devsu.clientes;

import com.devsu.clientes.domain.Cliente;
import com.devsu.clientes.dto.ClientePatchRequest;
import com.devsu.clientes.dto.ClienteRequest;
import com.devsu.clientes.dto.ClienteResponse;
import com.devsu.clientes.exception.DuplicateResourceException;
import com.devsu.clientes.exception.ResourceNotFoundException;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.service.ClienteEventService;
import com.devsu.clientes.service.impl.ClienteServiceImpl;
import com.devsu.clientes.service.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ClienteRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ClienteEventService clienteEventService;

    private final ClienteMapper mapper = new ClienteMapper();
    private ClienteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClienteServiceImpl(repository, mapper, passwordEncoder, clienteEventService);
    }

    private ClienteRequest nuevoRequest() {
        return ClienteRequest.builder()
                .nombre("Jose Lema").genero("M").edad(30)
                .identificacion("0102030405").direccion("Otavalo sn y principal")
                .telefono("098254785").clienteId("jlema")
                .contrasena("1234").estado(true)
                .build();
    }

    private Cliente clienteGuardado() {
        return Cliente.builder()
                .id(1L).nombre("Jose Lema").genero("M").edad(30)
                .identificacion("0102030405").direccion("Otavalo sn y principal")
                .telefono("098254785").clienteId("jlema")
                .contrasena("$2a$hashed").estado(true)
                .build();
    }

    @Test
    @DisplayName("Crear cliente: cifra la contraseña, persiste y publica el evento")
    void crearCliente_ok() {
        when(repository.existsByClienteId(anyString())).thenReturn(false);
        when(repository.existsByIdentificacion(anyString())).thenReturn(false);
        when(passwordEncoder.encode("1234")).thenReturn("$2a$hashed");
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> {
            Cliente c = inv.getArgument(0);
            c.setId(1L);
            return c;
        });

        ClienteResponse response = service.crear(nuevoRequest());

        assertThat(response.getClienteId()).isEqualTo("jlema");
        assertThat(response.getEstado()).isTrue();

        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getContrasena()).isEqualTo("$2a$hashed");

        verify(clienteEventService).publicarCreado(any(Cliente.class));
    }

    @Test
    @DisplayName("Crear cliente con identificacion duplicada lanza DuplicateResourceException")
    void crearCliente_identificacionDuplicada() {
        when(repository.existsByClienteId(anyString())).thenReturn(false);
        when(repository.existsByIdentificacion("0102030405")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(nuevoRequest()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(repository, never()).save(any());
        verify(clienteEventService, never()).publicarCreado(any());
    }

    @Test
    @DisplayName("Listar clientes devuelve la lista completa")
    void listarClientes_ok() {
        when(repository.findAll()).thenReturn(List.of(clienteGuardado()));

        List<ClienteResponse> result = service.listar();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getClienteId()).isEqualTo("jlema");
    }
}
