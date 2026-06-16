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
    @DisplayName("Crear cliente con clienteId duplicado lanza DuplicateResourceException")
    void crearCliente_clienteIdDuplicado() {
        when(repository.existsByClienteId("jlema")).thenReturn(true);

        assertThatThrownBy(() -> service.crear(nuevoRequest()))
                .isInstanceOf(DuplicateResourceException.class);

        verify(repository, never()).save(any());
        verify(clienteEventService, never()).publicarCreado(any());
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

    @Test
    @DisplayName("Obtener cliente existente devuelve el response")
    void obtenerCliente_ok() {
        when(repository.findByClienteId("jlema")).thenReturn(Optional.of(clienteGuardado()));

        ClienteResponse response = service.obtenerPorClienteId("jlema");

        assertThat(response.getNombre()).isEqualTo("Jose Lema");
    }

    @Test
    @DisplayName("Obtener cliente inexistente lanza ResourceNotFoundException")
    void obtenerCliente_noExiste() {
        when(repository.findByClienteId("xxx")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.obtenerPorClienteId("xxx"))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Actualizar cliente persiste los cambios y publica el evento")
    void actualizarCliente_ok() {
        Cliente existente = clienteGuardado();
        when(repository.findByClienteId("jlema")).thenReturn(Optional.of(existente));
        when(passwordEncoder.encode(anyString())).thenReturn("$2a$nuevo");
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClienteRequest request = nuevoRequest();
        request.setNombre("Jose Lema Actualizado");

        ClienteResponse response = service.actualizar("jlema", request);

        assertThat(response.getNombre()).isEqualTo("Jose Lema Actualizado");
        verify(clienteEventService).publicarActualizado(any(Cliente.class));
    }

    @Test
    @DisplayName("Actualizar cliente inexistente lanza ResourceNotFoundException")
    void actualizarCliente_noExiste() {
        when(repository.findByClienteId("xxx")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.actualizar("xxx", nuevoRequest()))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(clienteEventService, never()).publicarActualizado(any());
    }

    @Test
    @DisplayName("Actualizar parcial solo modifica los campos enviados")
    void actualizarParcialCliente_ok() {
        Cliente existente = clienteGuardado();
        when(repository.findByClienteId("jlema")).thenReturn(Optional.of(existente));
        when(repository.save(any(Cliente.class))).thenAnswer(inv -> inv.getArgument(0));

        ClientePatchRequest patch = new ClientePatchRequest();
        patch.setNombre("Jose Lema Patch");

        ClienteResponse response = service.actualizarParcial("jlema", patch);

        assertThat(response.getNombre()).isEqualTo("Jose Lema Patch");
        assertThat(response.getTelefono()).isEqualTo("098254785");
        verify(clienteEventService).publicarActualizado(any(Cliente.class));
    }

    @Test
    @DisplayName("Eliminar cliente lo borra y publica el evento")
    void eliminarCliente_ok() {
        when(repository.findByClienteId("jlema")).thenReturn(Optional.of(clienteGuardado()));

        service.eliminar("jlema");

        verify(repository).delete(any(Cliente.class));
        verify(clienteEventService).publicarEliminado(any(Cliente.class));
    }

    @Test
    @DisplayName("Eliminar cliente inexistente lanza ResourceNotFoundException")
    void eliminarCliente_noExiste() {
        when(repository.findByClienteId("xxx")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.eliminar("xxx"))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(repository, never()).delete(any());
        verify(clienteEventService, never()).publicarEliminado(any());
    }
}
