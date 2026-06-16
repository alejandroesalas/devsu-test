package com.devsu.cuentas.service.impl;

import com.devsu.cuentas.domain.ClienteView;
import com.devsu.cuentas.domain.Cuenta;
import com.devsu.cuentas.domain.Movimiento;
import com.devsu.cuentas.dto.CuentaSaldoItem;
import com.devsu.cuentas.dto.ReporteMovimientoItem;
import com.devsu.cuentas.dto.ReporteResponse;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.repository.ClienteViewRepository;
import com.devsu.cuentas.repository.CuentaRepository;
import com.devsu.cuentas.repository.MovimientoRepository;
import com.devsu.cuentas.service.ReporteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("d/M/yyyy");

    private final CuentaRepository cuentaRepository;
    private final MovimientoRepository movimientoRepository;
    private final ClienteViewRepository clienteViewRepository;

    @Override
    @Transactional(readOnly = true)
    public ReporteResponse generar(String clienteId, LocalDate fechaInicio, LocalDate fechaFin) {
        ClienteView cliente = clienteViewRepository.findById(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + clienteId));

        LocalDateTime inicio = fechaInicio.atStartOfDay();
        LocalDateTime fin = fechaFin.atTime(LocalTime.MAX);

        List<Cuenta> cuentas = cuentaRepository.findByClienteId(clienteId);

        List<CuentaSaldoItem> resumenCuentas = cuentas.stream()
                .map(c -> CuentaSaldoItem.builder()
                        .numeroCuenta(c.getNumeroCuenta())
                        .tipoCuenta(c.getTipoCuenta())
                        .saldoDisponible(c.getSaldoDisponible())
                        .build())
                .collect(Collectors.toList());

        List<Movimiento> movimientos = movimientoRepository.findByClienteAndRango(clienteId, inicio, fin);

        List<ReporteMovimientoItem> detalle = movimientos.stream()
                .map(m -> {
                    Cuenta c = m.getCuenta();
                    return ReporteMovimientoItem.builder()
                            .fecha(m.getFecha().format(FMT))
                            .cliente(cliente.getNombre())
                            .numeroCuenta(c.getNumeroCuenta())
                            .tipo(c.getTipoCuenta().name())
                            .saldoInicial(c.getSaldoInicial())
                            .estado(c.getEstado())
                            .movimiento(m.getValor())
                            .saldoDisponible(m.getSaldo())
                            .build();
                })
                .collect(Collectors.toList());

        return ReporteResponse.builder()
                .cliente(cliente.getNombre())
                .clienteId(clienteId)
                .fechaInicio(fechaInicio.format(FMT))
                .fechaFin(fechaFin.format(FMT))
                .cuentas(resumenCuentas)
                .movimientos(detalle)
                .build();
    }
}
