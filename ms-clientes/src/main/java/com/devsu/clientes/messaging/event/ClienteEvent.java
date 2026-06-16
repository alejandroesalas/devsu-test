package com.devsu.clientes.messaging.event;

import lombok.*;

import java.io.Serializable;

/** Evento publicado a RabbitMQ cuando un Cliente cambia. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClienteEvent implements Serializable {
    public enum Tipo { CREATED, UPDATED, DELETED }

    private Tipo tipo;
    private String clienteId;
    private String nombre;
    private Boolean estado;
}
