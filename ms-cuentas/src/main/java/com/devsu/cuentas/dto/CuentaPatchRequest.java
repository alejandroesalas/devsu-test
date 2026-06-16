package com.devsu.cuentas.dto;

import com.devsu.cuentas.domain.TipoCuenta;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CuentaPatchRequest {
    private TipoCuenta tipoCuenta;
    private Boolean estado;
}
