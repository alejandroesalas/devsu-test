package com.devsu.cuentas.service.mapper;

import com.devsu.cuentas.domain.Movimiento;
import com.devsu.cuentas.dto.MovimientoResponse;
import org.springframework.stereotype.Component;

@Component
public class MovimientoMapper {
    public MovimientoResponse toResponse(Movimiento m) {
        return MovimientoResponse.builder()
                .id(m.getId())
                .fecha(m.getFecha())
                .numeroCuenta(m.getCuenta().getNumeroCuenta())
                .tipoMovimiento(m.getTipoMovimiento())
                .valor(m.getValor())
                .saldo(m.getSaldo())
                .build();
    }
}
