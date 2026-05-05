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
        // Crear una cuenta con ID válido y saldo positivo
        Cuenta cuenta = new Cuenta("C-001", 5000);
        // Verificar que el ID se almacenó correctamente
        assertEquals("C-001", cuenta.getIdCuenta());
        // Verificar que el saldo inicial se almacenó correctamente
        assertEquals(5000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Constructor con saldo cero")
    void constructorConSaldoCero() {
        // Crear una cuenta con saldo inicial de cero (caso límite válido)
        Cuenta cuenta = new Cuenta("C-ZERO", 0);
        // Verificar que el ID se almacenó
        assertEquals("C-ZERO", cuenta.getIdCuenta());
        // Verificar que el saldo cero es aceptado
        assertEquals(0, cuenta.getSaldo());
    }

    @ParameterizedTest
    @CsvSource({
            "null",      // Caso: ID nulo
            "''",        // Caso: ID vacío
            "'   '"      // Caso: ID solo espacios en blanco
    })
    @DisplayName("Rechazar constructor con ID nulo, vacío o en blanco")
    void rechazarConstructorConIdInvalido(String idInvalido) {
        // Convertir el string "null" a valor null real, mantener otros strings como están
        String idParaTest = idInvalido.equals("null") ? null : idInvalido;
        // Capturar y verificar que se lanza IllegalArgumentException al crear cuenta con ID inválido
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta(idParaTest, 1000)
        );
        // Verificar que el mensaje de error es exactamente el esperado
        assertEquals("El id de cuenta no puede ser vacío", exception.getMessage());
    }

    @Test
    @DisplayName("Rechazar constructor con saldo negativo")
    void rechazarConstructorConSaldoNegativo() {
        // Capturar excepción al intentar crear cuenta con saldo negativo
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> new Cuenta("C-NEG", -500)
        );
        // Verificar mensaje de error específico para saldo negativo
        assertEquals("El saldo inicial no puede ser negativo", exception.getMessage());
    }

    // ==================== Depositar Tests (parameterized) ====================

    @ParameterizedTest
    @CsvSource({
            "1000, 500, 1500",           // Caso: depositar cantidad redonda (1000 + 500 = 1500)
            "1000, 0.01, 1000.01",       // Caso: depositar decimal pequeño
            "1000, 1000000, 1001000"     // Caso: depositar cantidad muy grande
    })
    @DisplayName("Depositar varios montos (parameterized)")
    void depositarVariosMontosParameterized(double inicial, double deposito, double esperado) {
        // Crear cuenta con el saldo inicial especificado
        Cuenta cuenta = new Cuenta("C-DEP-PARAM", inicial);
        // Realizar el depósito especificado
        cuenta.depositar(deposito);
        // Verificar resultado según el tipo de número:
        // Si es número entero (sin decimales significativos), comparar como long
        if (Math.abs(esperado - Math.round(esperado)) < 1e-9) {
            // Conversión a long para números enteros exactos
            assertEquals((long) esperado, (long) cuenta.getSaldo());
        } else {
            // Para números con decimales, usar tolerancia de 0.001 para evitar errores de precisión
            assertEquals(esperado, cuenta.getSaldo(), 0.001);
        }
    }

    @Test
    @DisplayName("Depositar montos múltiples consecutivos")
    void depositarMultiplesConsecutivos() {
        // Crear cuenta inicial con 100
        Cuenta cuenta = new Cuenta("C-DEP-002", 100);
        // Primer depósito: 100 + 50 = 150
        cuenta.depositar(50);
        // Segundo depósito: 150 + 75 = 225
        cuenta.depositar(75);
        // Tercer depósito: 225 + 25 = 250
        cuenta.depositar(25);
        // Verificar que el saldo final es la suma correcta
        assertEquals(250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar depositar monto negativo")
    void rechazarDepositoNegativo() {
        // Crear cuenta con 1000
        Cuenta cuenta = new Cuenta("C-DEP-NEG", 1000);
        // Capturar excepción al intentar depositar monto negativo
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(-100)
        );
        // Verificar el mensaje de error
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        // Verificar que el saldo no cambió (se mantiene en 1000)
        assertEquals(1000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar depositar monto cero")
    void rechazarDepositoCero() {
        // Crear cuenta con 1000
        Cuenta cuenta = new Cuenta("C-DEP-ZERO", 1000);
        // Capturar excepción al intentar depositar cero
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.depositar(0)
        );
        // Verificar el mensaje de error
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        // Verificar que el saldo no cambió
        assertEquals(1000, cuenta.getSaldo());
    }

    // ==================== Retirar Tests ====================

    @Test
    @DisplayName("Retirar monto válido")
    void retirarMontoValido() {
        // Crear cuenta con 2000
        Cuenta cuenta = new Cuenta("C-RET", 2000);
        // Retirar 750: 2000 - 750 = 1250
        cuenta.retirar(750);
        // Verificar saldo resultante
        assertEquals(1250, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Retirar todo el saldo")
    void retirarTodoElSaldo() {
        // Crear cuenta con 500
        Cuenta cuenta = new Cuenta("C-RET-ALL", 500);
        // Retirar toda la cantidad: 500 - 500 = 0
        cuenta.retirar(500);
        // Verificar que el saldo resultante es cero
        assertEquals(0, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar más saldo disponible")
    void rechazarRetiroMasDelSaldo() {
        // Crear cuenta con 300
        Cuenta cuenta = new Cuenta("C-RET-INSUF", 300);
        // Capturar excepción al intentar retirar 500 (más del disponible)
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(500)
        );
        // Verificar mensaje de error por saldo insuficiente
        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        // Verificar que el saldo no cambió (se mantiene en 300)
        assertEquals(300, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar monto negativo")
    void rechazarRetiroNegativo() {
        // Crear cuenta con 1000
        Cuenta cuenta = new Cuenta("C-RET-NEG", 1000);
        // Capturar excepción al intentar retirar monto negativo
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(-100)
        );
        // Verificar el mensaje de error
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        // Verificar que el saldo no cambió
        assertEquals(1000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Rechazar retirar monto cero")
    void rechazarRetiroCero() {
        // Crear cuenta con 1000
        Cuenta cuenta = new Cuenta("C-RET-ZERO", 1000);
        // Capturar excepción al intentar retirar cero
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuenta.retirar(0)
        );
        // Verificar el mensaje de error
        assertEquals("El monto debe ser mayor que cero", exception.getMessage());
        // Verificar que el saldo no cambió
        assertEquals(1000, cuenta.getSaldo());
    }

    // ==================== Integration Tests ====================

    @Test
    @DisplayName("Flujo completo: crear, depositar, retirar, consultar")
    void flujoCompleto() {
        // Crear cuenta inicial con 1000
        Cuenta cuenta = new Cuenta("C-FLUJO", 1000);
        // Verificar saldo inicial
        assertEquals(1000, cuenta.getSaldo());
        
        // Primer depósito: 1000 + 500 = 1500
        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());
        
        // Segundo depósito: 1500 + 250 = 1750
        cuenta.depositar(250);
        assertEquals(1750, cuenta.getSaldo());
        
        // Primer retiro: 1750 - 300 = 1450
        cuenta.retirar(300);
        assertEquals(1450, cuenta.getSaldo());
        
        // Segundo retiro: 1450 - 450 = 1000
        cuenta.retirar(450);
        assertEquals(1000, cuenta.getSaldo());
        
        // Verificar que el ID permanece igual después de todas las operaciones
        assertEquals("C-FLUJO", cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Múltiples depósitos y retiros alternados")
    void operacionesAlternadas() {
        // Crear cuenta inicial con 5000
        Cuenta cuenta = new Cuenta("C-ALT", 5000);
        
        // Retiro 1: 5000 - 1000 = 4000
        cuenta.retirar(1000);
        
        // Depósito 1: 4000 + 2000 = 6000
        cuenta.depositar(2000);
        
        // Retiro 2: 6000 - 500 = 5500
        cuenta.retirar(500);
        
        // Depósito 2: 5500 + 1500 = 7000
        cuenta.depositar(1500);
        
        // Retiro 3: 7000 - 3000 = 4000
        cuenta.retirar(3000);
        
        // Verificar que el saldo final es correcto después de todas las operaciones alternadas
        assertEquals(4000, cuenta.getSaldo());
    }

    @Test
    @DisplayName("Validar que getIdCuenta retorna el ID correcto")
    void validarGetIdCuenta() {
        // Definir ID esperado con caracteres especiales y números
        String idEsperado = "CUENTA-ESPECIAL-123";
        // Crear cuenta con este ID
        Cuenta cuenta = new Cuenta(idEsperado, 1000);
        // Verificar que getIdCuenta retorna exactamente el mismo ID (sin cambios)
        assertEquals(idEsperado, cuenta.getIdCuenta());
    }

    @Test
    @DisplayName("Validar que getSaldo retorna el saldo correcto después de operaciones")
    void validarGetSaldoMultiples() {
        // Crear cuenta con 1000
        Cuenta cuenta = new Cuenta("C-GET-SALDO", 1000);
        // Verificar saldo inicial
        assertEquals(1000, cuenta.getSaldo());
        
        // Después de depósito de 500: 1000 + 500 = 1500
        cuenta.depositar(500);
        assertEquals(1500, cuenta.getSaldo());
        
        // Después de retiro de 300: 1500 - 300 = 1200
        cuenta.retirar(300);
        assertEquals(1200, cuenta.getSaldo());
        
        // Después de depósito de 100: 1200 + 100 = 1300
        cuenta.depositar(100);
        assertEquals(1300, cuenta.getSaldo());
    }
}
