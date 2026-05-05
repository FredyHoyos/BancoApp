package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Cuenta Model - Extended Coverage Tests")
class CuentaExtendedTest {

    // ==================== Constructor Tests ====================

    @Test
    @DisplayName("Constructor válido con ID y saldo positivo")
    void constructorValido() {
        // Act
        Cuenta cuenta = new Cuenta("C-001", 5000);

        // Assert
        assertEquals("C-001", cuenta.getIdCuenta());
        assertEquals(5000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Constructor con saldo cero")
    void constructorConSaldoCero() {
        // Act
        Cuenta cuenta = new Cuenta("C-ZERO", 0);

        // Assert
        assertEquals("C-ZERO", cuenta.getIdCuenta());
        assertEquals(0, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar constructor con ID nulo")
    void rechazarConstructorConIdNulo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta(null, 1000)
        );

        assertEquals("El id de cuenta no puede ser vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Rechazar constructor con ID vacío")
    void rechazarConstructorConIdVacio() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta("", 1000)
        );

        assertEquals("El id de cuenta no puede ser vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Rechazar constructor con ID en blanco")
    void rechazarConstructorConIdEnBlanco() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta("   ", 1000)
        );

        assertEquals("El id de cuenta no puede ser vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Rechazar constructor con saldo negativo")
    void rechazarConstructorConSaldoNegativo() {
        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta("C-NEG", -500)
        );

        assertEquals("El saldo inicial no puede ser negativo", exception.getMessage());
    }

    // ==================== Depositar Tests ====================

    @Test
    @DisplayName("Depositar monto válido")
    void depositarMontoValido() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP", 1000);

        // Act
        cuenta.depositar(500);

        // Assert
        assertEquals(1500, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Depositar múltiples montos")
    void depositarMultiplesMontos() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-MULTI", 100);

        // Act
        cuenta.depositar(50);
        cuenta.depositar(75);
        cuenta.depositar(25);

        // Assert
        assertEquals(250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Depositar monto muy pequeño")
    void depositarMontoMuyPequeno() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-SMALL", 1000);

        // Act
        cuenta.depositar(0.01);

        // Assert
        assertEquals(1000.01, cuenta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Depositar monto muy grande")
    void depositarMontoMuyGrande() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-LARGE", 1000);

        // Act
        cuenta.depositar(1_000_000);

        // Assert
        assertEquals(1_001_000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar depositar monto negativo")
    void rechazarDepositoNegativo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-NEG", 1000);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(-100)
        );

        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo()); // Saldo sin cambios
    }

    @Test
    @DisplayName("Rechazar depositar monto cero")
    void rechazarDepositoCero() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-ZERO", 1000);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(0)
        );

        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo()); // Saldo sin cambios
    }

    // ==================== Retirar Tests ====================

    @Test
    @DisplayName("Retirar monto válido")
    void retirarMontoValido() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET", 2000);

        // Act
        cuenta.retirar(750);

        // Assert
        assertEquals(1250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Retirar todo el saldo")
    void retirarTodoElSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-ALL", 500);

        // Act
        cuenta.retirar(500);

        // Assert
        assertEquals(0, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Retirar casi todo el saldo")
    void retirarCasiTodoElSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-ALMOST", 1000);

        // Act
        cuenta.retirar(999.99);

        // Assert
        assertEquals(0.01, cuenta.getSaldo(), 0.001);
    }

    @Test
    @DisplayName("Rechazar retirar más saldo disponible")
    void rechazarRetiroMasDelSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-INSUF", 300);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(500)
        );

        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        assertEquals(300, cuenta.getSaldo()); // Saldo sin cambios
    }

    @Test
    @DisplayName("Rechazar retirar exactamente más del saldo")
    void rechazarRetiroConDiferenciaMenor() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-EDGE", 100);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(100.01)
        );

        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        assertEquals(100, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar monto negativo")
    void rechazarRetiroNegativo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-NEG", 1000);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(-100)
        );

        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo()); // Saldo sin cambios
    }

    @Test
    @DisplayName("Rechazar retirar monto cero")
    void rechazarRetiroCero() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-ZERO", 1000);

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(0)
        );

        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        assertEquals(1000, cuenta.getSaldo()); // Saldo sin cambios
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Flujo completo: crear, depositar, retirar, consultar")
    void flujoCompleto() {
        // Arrange & Act
        Cuenta cuenta = new Cuenta("C-FLUJO", 1000);
        assertEquals(1000, cuenta.getSaldo());

        // Deposit
        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());

        // Deposit again
        cuenta.depositar(250);
        assertEquals(1750, cuenta.getSaldo());

        // Withdraw
        cuenta.retirar(300);
        assertEquals(1450, cuenta.getSaldo());

        // Withdraw again
        cuenta.retirar(450);
        assertEquals(1000, cuenta.getSaldo());

        // Assert
        assertEquals("C-FLUJO", cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Múltiples depósitos y retiros alternados")
    void operacionesAlternadas() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-ALT", 5000);

        // Act
        cuenta.retirar(1000); // 4000
        cuenta.depositar(2000); // 6000
        cuenta.retirar(500); // 5500
        cuenta.depositar(1500); // 7000
        cuenta.retirar(3000); // 4000

        // Assert
        assertEquals(4000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Validar que getIdCuenta retorna el ID correcto")
    void validarGetIdCuenta() {
        // Arrange
        String idEsperado = "CUENTA-ESPECIAL-123";
        Cuenta cuenta = new Cuenta(idEsperado, 1000);

        // Act & Assert
        assertEquals(idEsperado, cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Validar que getSaldo retorna el saldo correcto después de operaciones")
    void validarGetSaldoMultiples() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-GET-SALDO", 1000);

        // Act & Assert - Initial
        assertEquals(1000, cuenta.getSaldo());

        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());

        cuenta.retirar(300);
        assertEquals(1200, cuenta.getSaldo());

        cuenta.depositar(100);
        assertEquals(1300, cuenta.getSaldo());
    }
}
