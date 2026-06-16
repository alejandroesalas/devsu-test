package com.devsu.clientes.controller;

import com.devsu.clientes.dto.ClientePatchRequest;
import com.devsu.clientes.dto.ClienteRequest;
import com.devsu.clientes.dto.ClienteResponse;
import com.devsu.clientes.service.ClienteService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/clientes")
@RequiredArgsConstructor
public class ClienteController {

    private final ClienteService service;

    @PostMapping
    public ResponseEntity<ClienteResponse> crear(@Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.crear(request));
    }

    @GetMapping
    public ResponseEntity<List<ClienteResponse>> listar() {
        return ResponseEntity.ok(service.listar());
    }

    @GetMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> obtener(@PathVariable String clienteId) {
        return ResponseEntity.ok(service.obtenerPorClienteId(clienteId));
    }

    @PutMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> actualizar(@PathVariable String clienteId,
                                                      @Valid @RequestBody ClienteRequest request) {
        return ResponseEntity.ok(service.actualizar(clienteId, request));
    }

    @PatchMapping("/{clienteId}")
    public ResponseEntity<ClienteResponse> actualizarParcial(@PathVariable String clienteId,
                                                            @RequestBody ClientePatchRequest request) {
        return ResponseEntity.ok(service.actualizarParcial(clienteId, request));
    }

    @DeleteMapping("/{clienteId}")
    public ResponseEntity<Void> eliminar(@PathVariable String clienteId) {
        service.eliminar(clienteId);
        return ResponseEntity.noContent().build();
    }
}
