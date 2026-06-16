package com.devsu.cuentas.service.impl;

import com.devsu.cuentas.domain.Cuenta;
import com.devsu.cuentas.domain.Movimiento;
import com.devsu.cuentas.domain.TipoMovimiento;
import com.devsu.cuentas.dto.MovimientoRequest;
import com.devsu.cuentas.dto.MovimientoResponse;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.exception.SaldoNoDisponibleException;
import com.devsu.cuentas.repository.CuentaRepository;
import com.devsu.cuentas.repository.MovimientoRepository;
import com.devsu.cuentas.service.MovimientoService;
import com.devsu.cuentas.service.mapper.MovimientoMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MovimientoServiceImpl implements MovimientoService {

    private final MovimientoRepository movimientoRepository;
    private final CuentaRepository cuentaRepository;
    private final MovimientoMapper mapper;

    @Override
    @Transactional
    public MovimientoResponse registrar(MovimientoRequest request) {
        Cuenta cuenta = cuentaRepository.findByNumeroCuenta(request.getNumeroCuenta())
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cuenta no encontrada: " + request.getNumeroCuenta()));

        BigDecimal valor = request.getValor();
        if (valor.signum() == 0) {
            throw new IllegalArgumentException("El valor del movimiento no puede ser cero");
        }

        // F2: el valor puede ser positivo (depósito) o negativo (retiro)
        BigDecimal nuevoSaldo = cuenta.getSaldoDisponible().add(valor);

        // F3: si el saldo resultante es negativo -> "Saldo no disponible"
        if (nuevoSaldo.signum() < 0) {
            throw new SaldoNoDisponibleException();
        }

        cuenta.setSaldoDisponible(nuevoSaldo);
        cuentaRepository.save(cuenta);

        Movimiento movimiento = Movimiento.builder()
                .fecha(LocalDateTime.now())
                .tipoMovimiento(valor.signum() > 0 ? TipoMovimiento.DEPOSITO : TipoMovimiento.RETIRO)
                .valor(valor)
                .saldo(nuevoSaldo)
                .cuenta(cuenta)
                .build();

        return mapper.toResponse(movimientoRepository.save(movimiento));
    }

    @Override
    @Transactional(readOnly = true)
    public List<MovimientoResponse> listar() {
        return movimientoRepository.findAll().stream()
                .map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public MovimientoResponse obtener(Long id) {
        return movimientoRepository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new ResourceNotFoundException("Movimiento no encontrado: " + id));
    }
}
