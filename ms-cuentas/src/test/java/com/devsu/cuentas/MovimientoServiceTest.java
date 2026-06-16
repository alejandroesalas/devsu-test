package com.devsu.cuentas;

import com.devsu.cuentas.domain.Cuenta;
import com.devsu.cuentas.domain.Movimiento;
import com.devsu.cuentas.domain.TipoCuenta;
import com.devsu.cuentas.domain.TipoMovimiento;
import com.devsu.cuentas.dto.MovimientoRequest;
import com.devsu.cuentas.dto.MovimientoResponse;
import com.devsu.cuentas.repository.CuentaRepository;
import com.devsu.cuentas.repository.MovimientoRepository;
import com.devsu.cuentas.service.impl.MovimientoServiceImpl;
import com.devsu.cuentas.service.mapper.MovimientoMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovimientoServiceTest {

    @Mock private MovimientoRepository movimientoRepository;
    @Mock private CuentaRepository cuentaRepository;
    @Mock private MovimientoMapper mapper;

    private MovimientoServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new MovimientoServiceImpl(movimientoRepository, cuentaRepository, mapper);
    }

    private Cuenta cuentaConSaldo(BigDecimal saldo) {
        return Cuenta.builder()
                .id(Long.valueOf(1L))
                .numeroCuenta("478758")
                .tipoCuenta(TipoCuenta.AHORROS)
                .saldoInicial(saldo)
                .saldoDisponible(saldo)
                .estado(Boolean.TRUE)
                .clienteId("jlema")
                .build();
    }

    private MovimientoResponse responseStub(BigDecimal valor, BigDecimal saldo, TipoMovimiento tipo) {
        return MovimientoResponse.builder()
                .id(Long.valueOf(1L))
                .fecha(LocalDateTime.now())
                .numeroCuenta("478758")
                .tipoMovimiento(tipo)
                .valor(valor)
                .saldo(saldo)
                .build();
    }

    @Test
    void registrar_deposito_aumentaSaldoYretornaTipoDeposito() {
        Cuenta cuenta = cuentaConSaldo(new BigDecimal("500.00"));
        MovimientoRequest request = new MovimientoRequest("478758", new BigDecimal("200.00"));
        MovimientoResponse expected = responseStub(new BigDecimal("200.00"), new BigDecimal("700.00"), TipoMovimiento.DEPOSITO);

        when(cuentaRepository.findByNumeroCuenta("478758")).thenReturn(Optional.of(cuenta));
        when(movimientoRepository.save(any(Movimiento.class))).thenAnswer(i -> i.getArgument(0));
        when(mapper.toResponse(any(Movimiento.class))).thenReturn(expected);

        MovimientoResponse result = service.registrar(request);

        assertThat(result.getTipoMovimiento()).isEqualTo(TipoMovimiento.DEPOSITO);
        assertThat(cuenta.getSaldoDisponible()).isEqualByComparingTo("700.00");
        verify(cuentaRepository).save(cuenta);
    }
}
