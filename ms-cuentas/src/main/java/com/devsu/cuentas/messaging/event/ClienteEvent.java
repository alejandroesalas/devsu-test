package com.devsu.cuentas.messaging.event;

import lombok.*;

import java.io.Serializable;

/** Contrato del evento recibido desde ms-clientes vía RabbitMQ. */
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
