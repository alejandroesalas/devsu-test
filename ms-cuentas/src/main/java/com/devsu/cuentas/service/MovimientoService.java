package com.devsu.cuentas.service;

import com.devsu.cuentas.dto.MovimientoRequest;
import com.devsu.cuentas.dto.MovimientoResponse;

import java.util.List;

public interface MovimientoService {
    MovimientoResponse registrar(MovimientoRequest request);
    List<MovimientoResponse> listar();
    MovimientoResponse obtener(Long id);
}
