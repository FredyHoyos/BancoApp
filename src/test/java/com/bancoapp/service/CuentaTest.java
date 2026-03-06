package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CuentaTest {

    @Test
    @DisplayName("Unit Test simple del modelo: getSaldo después de depositar")
    void debeActualizarSaldoAlDepositar() {
        // AAA - Arrange
        Cuenta cuenta = new Cuenta("M-1", 100);

        // AAA - Act
        cuenta.depositar(50);

        // AAA - Assert
        assertEquals(150, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Unit Test simple del modelo: no permitir monto negativo")
    void noDebePermitirMontoNegativo() {
        // AAA - Arrange
        Cuenta cuenta = new Cuenta("M-2", 100);

        // AAA - Act + Assert
        assertThrows(IllegalArgumentException.class, () -> cuenta.depositar(-1));
    }
}
