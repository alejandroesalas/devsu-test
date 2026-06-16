package com.devsu.cuentas.service.mapper;

import com.devsu.cuentas.domain.Cuenta;
import com.devsu.cuentas.dto.CuentaResponse;
import org.springframework.stereotype.Component;

@Component
public class CuentaMapper {
    public CuentaResponse toResponse(Cuenta c) {
        return CuentaResponse.builder()
                .id(c.getId())
                .numeroCuenta(c.getNumeroCuenta())
                .tipoCuenta(c.getTipoCuenta())
                .saldoInicial(c.getSaldoInicial())
                .saldoDisponible(c.getSaldoDisponible())
                .estado(c.getEstado())
                .clienteId(c.getClienteId())
                .build();
    }
}
