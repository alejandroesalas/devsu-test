package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.CuentaPatchRequest;
import com.devsu.cuentas.dto.CuentaRequest;
import com.devsu.cuentas.dto.CuentaResponse;

import java.util.List;

public interface CuentaService {
    CuentaResponse crear(CuentaRequest request);
    List<CuentaResponse> listar();
    CuentaResponse obtenerPorNumero(String numeroCuenta);
    CuentaResponse actualizar(String numeroCuenta, CuentaRequest request);
    CuentaResponse actualizarParcial(String numeroCuenta, CuentaPatchRequest request);
}
