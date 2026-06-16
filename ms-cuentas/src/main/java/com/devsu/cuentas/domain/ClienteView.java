package com.devsu.cuentas.domain;

import jakarta.persistence.*;
import lombok.*;

/**
 * Copia local (read-model) del Cliente, alimentada de forma asíncrona por
 * eventos de RabbitMQ provenientes de ms-clientes. Permite construir el reporte
 * sin acoplarse sincrónicamente al otro microservicio.
 */
@Entity
@Table(name = "cliente_view")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteView {

    @Id
    @Column(name = "cliente_id")
    private String clienteId;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private Boolean estado;
}
