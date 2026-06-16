package com.devsu.cuentas.dto;

import com.devsu.cuentas.domain.TipoMovimiento;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MovimientoResponse {
    private Long id;
    private LocalDateTime fecha;
    private String numeroCuenta;
    private TipoMovimiento tipoMovimiento;
    private BigDecimal valor;
    private BigDecimal saldo;
}
