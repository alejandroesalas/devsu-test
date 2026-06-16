package com.devsu.clientes.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Cliente hereda de Persona (herencia JOINED). Posee su propia clave de negocio
 * (clienteId), única.
 */
@Entity
@Table(name = "clientes")
@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
public class Cliente extends Persona {

    @Column(name = "cliente_id", nullable = false, unique = true)
    private String clienteId;

    @Column(nullable = false)
    private String contrasena;

    @Column(nullable = false)
    private Boolean estado;
}
