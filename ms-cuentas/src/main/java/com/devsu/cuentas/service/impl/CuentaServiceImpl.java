package com.devsu.cuentas.service.impl;

import com.devsu.cuentas.domain.ClienteView;
import com.devsu.cuentas.domain.Cuenta;
import com.devsu.cuentas.dto.CuentaPatchRequest;
import com.devsu.cuentas.dto.CuentaRequest;
import com.devsu.cuentas.dto.CuentaResponse;
import com.devsu.cuentas.exception.ClienteNoValidoException;
import com.devsu.cuentas.exception.DuplicateResourceException;
import com.devsu.cuentas.exception.ResourceNotFoundException;
import com.devsu.cuentas.repository.ClienteViewRepository;
import com.devsu.cuentas.repository.CuentaRepository;
import com.devsu.cuentas.service.CuentaService;
import com.devsu.cuentas.service.mapper.CuentaMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CuentaServiceImpl implements CuentaService {

    private final CuentaRepository repository;
    private final ClienteViewRepository clienteViewRepository;
    private final CuentaMapper mapper;

    @Override
    @Transactional
    public CuentaResponse crear(CuentaRequest request) {
        if (repository.existsByNumeroCuenta(request.getNumeroCuenta())) {
            throw new DuplicateResourceException("Ya existe la cuenta: " + request.getNumeroCuenta());
        }
        // Validación contra el read-model replicado de forma asíncrona desde ms-clientes
        ClienteView cliente = clienteViewRepository.findById(request.getClienteId())
                .orElseThrow(() -> new ClienteNoValidoException(
                        "El cliente '" + request.getClienteId() + "' no existe o aún no se ha sincronizado"));
        if (Boolean.FALSE.equals(cliente.getEstado())) {
            throw new ClienteNoValidoException("El cliente '" + request.getClienteId() + "' está inactivo");
        }

        Cuenta cuenta = Cuenta.builder()
                .numeroCuenta(request.getNumeroCuenta())
                .tipoCuenta(request.getTipoCuenta())
                .saldoInicial(request.getSaldoInicial())
                .saldoDisponible(request.getSaldoInicial())
                .estado(request.getEstado())
                .clienteId(request.getClienteId())
                .build();
        return mapper.toResponse(repository.save(cuenta));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CuentaResponse> listar() {
        return repository.findAll().stream().map(mapper::toResponse).collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CuentaResponse obtenerPorNumero(String numeroCuenta) {
        return mapper.toResponse(buscar(numeroCuenta));
    }

    @Override
    @Transactional
    public CuentaResponse actualizar(String numeroCuenta, CuentaRequest request) {
        Cuenta cuenta = buscar(numeroCuenta);
        cuenta.setTipoCuenta(request.getTipoCuenta());
        cuenta.setEstado(request.getEstado());
        return mapper.toResponse(repository.save(cuenta));
    }

    @Override
    @Transactional
    public CuentaResponse actualizarParcial(String numeroCuenta, CuentaPatchRequest request) {
        Cuenta cuenta = buscar(numeroCuenta);
        if (request.getTipoCuenta() != null) cuenta.setTipoCuenta(request.getTipoCuenta());
        if (request.getEstado() != null) cuenta.setEstado(request.getEstado());
        return mapper.toResponse(repository.save(cuenta));
    }

    private Cuenta buscar(String numeroCuenta) {
        return repository.findByNumeroCuenta(numeroCuenta)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta no encontrada: " + numeroCuenta));
    }
}
