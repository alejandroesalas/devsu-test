package com.devsu.clientes.domain;

import jakarta.persistence.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

/**
 * Entidad base Persona. Usa herencia JOINED: Cliente comparte la PK de Persona
 * pero cada una se persiste en su propia tabla.
 */
@Entity
@Table(name = "personas")
@Inheritance(strategy = InheritanceType.JOINED)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class Persona {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String genero;

    @Column(nullable = false)
    private Integer edad;

    @Column(nullable = false, unique = true)
    private String identificacion;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String telefono;
}
