package com.devsu.cuentas;

import com.devsu.cuentas.domain.ClienteView;
import com.devsu.cuentas.domain.TipoCuenta;
import com.devsu.cuentas.dto.CuentaRequest;
import com.devsu.cuentas.dto.MovimientoRequest;
import com.devsu.cuentas.repository.ClienteViewRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * F6 - Prueba de integración: levanta el contexto completo (con H2), crea una
 * cuenta vía API, registra un movimiento y valida la regla de "Saldo no disponible".
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class MovimientoIntegrationTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;
    @Autowired private ClienteViewRepository clienteViewRepository;

    @BeforeEach
    void seedCliente() {
        clienteViewRepository.save(ClienteView.builder()
                .clienteId("mmontalvo").nombre("Marianela Montalvo").estado(true).build());
    }

    private void crearCuenta(String numero, BigDecimal saldoInicial) throws Exception {
        CuentaRequest cuenta = CuentaRequest.builder()
                .numeroCuenta(numero).tipoCuenta(TipoCuenta.CORRIENTE)
                .saldoInicial(saldoInicial).estado(true).clienteId("mmontalvo").build();
        mockMvc.perform(post("/cuentas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cuenta)))
                .andExpect(status().isCreated());
    }

    @Test
    @DisplayName("Depósito de 600 sobre saldo 100 -> saldo disponible 700")
    void deposito_actualizaSaldo() throws Exception {
        crearCuenta("225487", new BigDecimal("100"));

        MovimientoRequest mov = MovimientoRequest.builder()
                .numeroCuenta("225487").valor(new BigDecimal("600")).build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.tipoMovimiento").value("DEPOSITO"))
                .andExpect(jsonPath("$.saldo").value(700));
    }

    @Test
    @DisplayName("Retiro mayor al saldo -> 400 con mensaje 'Saldo no disponible' (F3)")
    void retiroSinSaldo_devuelveError() throws Exception {
        crearCuenta("495878", new BigDecimal("0"));

        MovimientoRequest mov = MovimientoRequest.builder()
                .numeroCuenta("495878").valor(new BigDecimal("-150")).build();

        mockMvc.perform(post("/movimientos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mov)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Saldo no disponible"));
    }
}
