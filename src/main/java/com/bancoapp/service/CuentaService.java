package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import com.bancoapp.repository.CuentaRepository;

public class CuentaService {

    private final CuentaRepository cuentaRepository;

    public CuentaService(CuentaRepository cuentaRepository) {
        this.cuentaRepository = cuentaRepository;
    }

    public void crearCuenta(String idCuenta, double saldoInicial) {
        // Verificar que la cuenta no exista ya
        if (cuentaRepository.buscarPorId(idCuenta).isPresent()) {
            throw new IllegalArgumentException("Ya existe una cuenta con el ID: " + idCuenta);
        }
        
        Cuenta cuenta = new Cuenta(idCuenta, saldoInicial);
        cuentaRepository.guardar(cuenta);
    }

    public void depositar(String idCuenta, double monto) {
        Cuenta cuenta = obtenerCuenta(idCuenta);
        cuenta.depositar(monto);
        cuentaRepository.guardar(cuenta);
    }

    public void retirar(String idCuenta, double monto) {
        Cuenta cuenta = obtenerCuenta(idCuenta);
        cuenta.retirar(monto);
        cuentaRepository.guardar(cuenta);
    }

    public double consultarSaldo(String idCuenta) {
        Cuenta cuenta = obtenerCuenta(idCuenta);
        return cuenta.getSaldo();
    }

    private Cuenta obtenerCuenta(String idCuenta) {
        return cuentaRepository.buscarPorId(idCuenta)
                .orElseThrow(() -> new IllegalArgumentException("Cuenta no encontrada: " + idCuenta));
    }
}
