package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cuenta Model - Extended Coverage Tests")
class CuentaExtendedTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor válido con ID y saldo positivo")
    void constructorValido() {
        Cuenta cuenta = new Cuenta("C-001", 5000);
        assertEquals("C-001", cuenta.getIdCuenta());
        assertEquals(5000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Constructor con saldo cero")
    void constructorConSaldoCero() {
        Cuenta cuenta = new Cuenta("C-ZERO", 0);
        assertEquals("C-ZERO", cuenta.getIdCuenta());
        assertEquals(0, cuenta.getSaldo());
    }

    @ParameterizedTest
    @CsvSource({
            "null",
            "''",
            "'   '"
    })
    @DisplayName("Rechazar constructor con ID nulo, vacío o en blanco")
    void rechazarConstructorConIdInvalido(String idInvalido) {
        String idParaTest = idInvalido.equals("null") ? null : idInvalido;
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta(idParaTest, 1000)
        );
        assertEquals("El id de cuenta no puede ser vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Rechazar constructor con saldo negativo")
    void rechazarConstructorConSaldoNegativo() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta("C-NEG", -500)
        );
        assertEquals("El saldo inicial no puede ser negativo", exception.getMessage());
    }

    // ==================== Depositar Tests (parameterized) ====================

    @ParameterizedTest
    @CsvSource({
            "1000, 500, 1500",
            "1000, 0.01, 1000.01",
            "1000, 1000000, 1001000"
    })
    @DisplayName("Depositar varios montos (parameterized)")
    void depositarVariosMontosParameterized(double inicial, double deposito, double esperado) {
        Cuenta cuenta = new Cuenta("C-DEP-PARAM", inicial);
        cuenta.depositar(deposito);
        if (Math.abs(esperado - Math.round(esperado)) < 1e-9) {
            assertEquals((long) esperado, (long) cuenta.getSaldo());
        } else {
            assertEquals(esperado, cuenta.getSaldo(), 0.001);
        }
    }

    @Test
    @DisplayName("Depositar montos múltiples consecutivos")
    void depositarMultiplesConsecutivos() {
        Cuenta cuenta = new Cuenta("C-DEP-002", 100);
        cuenta.depositar(50);
        cuenta.depositar(75);
        cuenta.depositar(25);
        assertEquals(250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar depositar monto negativo")
    void rechazarDepositoNegativo() {
        Cuenta cuenta = new Cuenta("C-DEP-NEG", 1000);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(-100)
        );
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar depositar monto cero")
    void rechazarDepositoCero() {
        Cuenta cuenta = new Cuenta("C-DEP-ZERO", 1000);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(0)
        );
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo());
    }

    // ==================== Retirar Tests ====================

    @Test
    @DisplayName("Retirar monto válido")
    void retirarMontoValido() {
        Cuenta cuenta = new Cuenta("C-RET", 2000);
        cuenta.retirar(750);
        assertEquals(1250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Retirar todo el saldo")
    void retirarTodoElSaldo() {
        Cuenta cuenta = new Cuenta("C-RET-ALL", 500);
        cuenta.retirar(500);
        assertEquals(0, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar más saldo disponible")
    void rechazarRetiroMasDelSaldo() {
        Cuenta cuenta = new Cuenta("C-RET-INSUF", 300);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(500)
        );
        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        assertEquals(300, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar monto negativo")
    void rechazarRetiroNegativo() {
        Cuenta cuenta = new Cuenta("C-RET-NEG", 1000);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(-100)
        );
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar monto cero")
    void rechazarRetiroCero() {
        Cuenta cuenta = new Cuenta("C-RET-ZERO", 1000);
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(0)
        );
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo());
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Flujo completo: crear, depositar, retirar, consultar")
    void flujoCompleto() {
        Cuenta cuenta = new Cuenta("C-FLUJO", 1000);
        assertEquals(1000, cuenta.getSaldo());
        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());
        cuenta.depositar(250);
        assertEquals(1750, cuenta.getSaldo());
        cuenta.retirar(300);
        assertEquals(1450, cuenta.getSaldo());
        cuenta.retirar(450);
        assertEquals(1000, cuenta.getSaldo());
        assertEquals("C-FLUJO", cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Múltiples depósitos y retiros alternados")
    void operacionesAlternadas() {
        Cuenta cuenta = new Cuenta("C-ALT", 5000);
        cuenta.retirar(1000);
        cuenta.depositar(2000);
        cuenta.retirar(500);
        cuenta.depositar(1500);
        cuenta.retirar(3000);
        assertEquals(4000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Validar que getIdCuenta retorna el ID correcto")
    void validarGetIdCuenta() {
        String idEsperado = "CUENTA-ESPECIAL-123";
        Cuenta cuenta = new Cuenta(idEsperado, 1000);
        assertEquals(idEsperado, cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Validar que getSaldo retorna el saldo correcto después de operaciones")
    void validarGetSaldoMultiples() {
        Cuenta cuenta = new Cuenta("C-GET-SALDO", 1000);
        assertEquals(1000, cuenta.getSaldo());
        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());
        cuenta.retirar(300);
        assertEquals(1200, cuenta.getSaldo());
        cuenta.depositar(100);
        assertEquals(1300, cuenta.getSaldo());
    }
}
