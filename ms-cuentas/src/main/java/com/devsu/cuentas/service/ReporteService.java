package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.ReporteResponse;

import java.time.LocalDate;

public interface ReporteService {
    ReporteResponse generar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin);
}
