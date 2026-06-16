package com.devsu.cuentas.dto;

import com.devsu.cuentas.domain.TipoCuenta;
import lombok.*;

import java.math.BigDecimal;

/** Resumen de cuenta asociada con su saldo actual (requisito F4). */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaSaldoItem {
    private String numeroCuenta;
    private TipoCuenta tipoCuenta;
    private BigDecimal saldoDisponible;
}
