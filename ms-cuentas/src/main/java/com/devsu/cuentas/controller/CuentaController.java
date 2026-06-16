package com.devsu.cuentas.controller;

import com.devsu.cuentas.dto.CuentaPatchRequest;
import com.devsu.cuentas.dto.CuentaRequest;
import com.devsu.cuentas.dto.CuentaResponse;
import com.devsu.cuentas.service.CuentaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cuentas")
@RequiredArgsConstructor
public class CuentaController {

    private final CuentaService service;

    @PostMapping
    public ResponseEntity<CuentaResponse> crear(@Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<CuentaResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> obtener(@PathVariable String numeroCuenta) {
        return ResponseEntity.ok(service.obtenerPorNumero(numeroCuenta));
    }

    @PutMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizar(@PathVariable String numeroCuenta,
                                                     @Valid @RequestBody CuentaRequest request) {
        return ResponseEntity.ok(service.actualizar(numeroCuenta, request));
    }

    @PatchMapping("/{numeroCuenta}")
    public ResponseEntity<CuentaResponse> actualizarParcial(@PathVariable String numeroCuenta,
                                                          @RequestBody CuentaPatchRequest request) {
        return ResponseEntity.ok(service.actualizarParcial(numeroCuenta, request));
    }
}
