package com.devsu.clientes.dto;

import jakarta.validation.constraints.Min;
import lombok.*;

/** DTO para actualización parcial (PATCH). Todos los campos son opcionales. */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientePatchRequest {
    private String nombre;
    private String genero;
    @Min(value = 0, message = "La edad no puede ser negativa")
    private Integer edad;
    private String direccion;
    private String telefono;
    private String contrasena;
    private Boolean estado;
}
