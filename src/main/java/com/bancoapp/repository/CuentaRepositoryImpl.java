package com.bancoapp.repository;

import com.bancoapp.model.Cuenta;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CuentaRepositoryImpl implements CuentaRepository {
    
    private final Map<String, Cuenta> cuentas = new HashMap<>();

    @Override
    public Optional<Cuenta> buscarPorId(String idCuenta) {
        return Optional.ofNullable(cuentas.get(idCuenta));
    }

    @Override
    public void guardar(Cuenta cuenta) {
        cuentas.put(cuenta.getIdCuenta(), cuenta);
    }

    public void eliminar(String idCuenta) {
        cuentas.remove(idCuenta);
    }

    public int cantidadCuentas() {
        return cuentas.size();
    }
}
