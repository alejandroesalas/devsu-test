package com.devsu.cuentas.dto;

import lombok.*;

import java.util.List;

/**
 * Reporte de Estado de Cuenta (F4). Contiene:
 *  - cuentas asociadas con sus saldos
 *  - detalle de movimientos en el rango de fechas
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReporteResponse {
    private String cliente;
    private String clienteId;
    private String fechaInicio;
    private String fechaFin;
    private List<CuentaSaldoItem> cuentas;
    private List<ReporteMovimientoItem> movimientos;
}
