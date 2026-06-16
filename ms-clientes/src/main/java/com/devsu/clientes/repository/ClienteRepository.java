package com.devsu.clientes.repository;

import com.devsu.clientes.domain.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    Optional<Cliente> findByClienteId(String clienteId);
    boolean existsByClienteId(String clienteId);
    boolean existsByIdentificacion(String identificacion);
}
