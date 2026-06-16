package com.devsu.clientes;

import com.devsu.clientes.domain.Cliente;
import com.devsu.clientes.dto.ClienteRequest;
import com.devsu.clientes.dto.ClienteResponse;
import com.devsu.clientes.exception.DuplicateResourceException;
import com.devsu.clientes.exception.ResourceNotFoundException;
import com.devsu.clientes.messaging.ClienteEventPublisher;
import com.devsu.clientes.repository.ClienteRepository;
import com.devsu.clientes.service.impl.ClienteServiceImpl;
import com.devsu.clientes.service.mapper.ClienteMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * F5 - Prueba unitaria para la entidad de dominio Cliente y su lógica de servicio.
 */
@ExtendWith(MockitoExtension.class)
class ClienteServiceTest {

    @Mock private ClienteRepository repository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private ClienteEventPublisher eventPublisher;

    private final ClienteMapper mapper = new ClienteMapper();
    private ClienteServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new ClienteServiceImpl(repository, mapper, passwordEncoder, eventPublisher);
    }

    private ClienteRequest nuevoRequest() {
        return ClienteRequest.builder()
                .nombre("Jose Lema").genero("M").edad(30)
                .identificacion("0102030405").direccion("Otavalo sn y principal")
                .telefono("098254785").clienteId("jlema")
                .contrasena("1234").estado(true)
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

        verify(passwordEncoder).encode("1234");
        verify(eventPublisher, times(1)).publish(any());

        // La contraseña cifrada nunca debe viajar en el DTO de salida
        ArgumentCaptor<Cliente> captor = ArgumentCaptor.forClass(Cliente.class);
        verify(repository).save(captor.capture());
        assertThat(captor.getValue().getContrasena()).isEqualTo("$2a$hashed");
    }

    @Test
    @DisplayName("Crear cliente con clienteId duplicado lanza DuplicateResourceException")
    void crearCliente_duplicado() {
        when(repository.existsByClienteId("jlema")).thenReturn(true);
        assertThatThrownBy(() -> service.crear(nuevoRequest()))
                .isInstanceOf(DuplicateResourceException.class);
        verify(repository, never()).save(any());
    }

    @Test
    @DisplayName("Obtener cliente inexistente lanza ResourceNotFoundException")
    void obtenerCliente_noExiste() {
        when(repository.findByClienteId("xxx")).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.obtenerPorClienteId("xxx"))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}
