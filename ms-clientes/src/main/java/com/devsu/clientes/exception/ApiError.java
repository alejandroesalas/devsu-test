package com.devsu.clientes.exception;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

/** Estructura uniforme de error para todas las respuestas de la API. */
@Getter
@Builder
@AllArgsConstructor
public class ApiError {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
    private Map<String, String> validationErrors;
}
