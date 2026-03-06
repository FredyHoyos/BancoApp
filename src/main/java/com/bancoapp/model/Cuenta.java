package com.bancoapp.model;

public class Cuenta {
    private final String idCuenta;
    private double saldo;

    public Cuenta(String idCuenta, double saldoInicial) {
        if (idCuenta == null || idCuenta.isBlank()) {
            throw new IllegalArgumentException("El id de cuenta no puede ser vacío");
        }
        if (saldoInicial < 0) {
            throw new IllegalArgumentException("El saldo inicial no puede ser negativo");
        }
        this.idCuenta = idCuenta;
        this.saldo = saldoInicial;
    }

    public String getIdCuenta() {
        return idCuenta;
    }

    public void depositar(double monto) {
        validarMontoPositivo(monto);
        this.saldo += monto;
    }

    public void retirar(double monto) {
        // TDD (paso 2): esta validación se implementa después de escribir
        // primero el test de retiro con saldo insuficiente.
        validarMontoPositivo(monto);
        if (monto > saldo) {
            throw new IllegalArgumentException("Saldo insuficiente para retirar");
        }
        this.saldo -= monto;
    }

    public double getSaldo() {
        return saldo;
    }

    private void validarMontoPositivo(double monto) {
        if (monto <= 0) {
            throw new IllegalArgumentException("El monto debe ser mayor que cero");
        }
    }
}
