package com.bancoapp.repository;

import com.bancoapp.model.Cuenta;

import java.util.Optional;

public interface CuentaRepository {
    Optional<Cuenta> buscarPorId(String idCuenta);

    void guardar(Cuenta cuenta);
}
