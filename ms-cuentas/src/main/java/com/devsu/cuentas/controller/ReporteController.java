package com.devsu.cuentas.controller;

import com.devsu.cuentas.dto.ReporteResponse;
import com.devsu.cuentas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;

/**
 * F4 - Reporte "Estado de Cuenta" por rango de fechas y cliente.
 * Ejemplo: GET /reportes?clienteId=jlema&fechaInicio=2022-02-01&fechaFin=2022-02-28
 */
@RestController
@RequestMapping("/reportes")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService service;

    @GetMapping
    public ResponseEntity<ReporteResponse> generar(
            @RequestParam String clienteId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {
        return ResponseEntity.ok(service.generar(clienteId, fechaInicio, fechaFin));
    }
}
