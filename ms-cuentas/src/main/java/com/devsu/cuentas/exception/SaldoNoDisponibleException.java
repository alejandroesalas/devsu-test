package com.devsu.cuentas.exception;

/** F3: se lanza cuando un movimiento dejaría el saldo en negativo. */
public class SaldoNoDisponibleException extends RuntimeException {
    public SaldoNoDisponibleException() {
        super("Saldo no disponible");
    }
}
