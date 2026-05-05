package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import com.bancoapp.repository.CuentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("CuentaService - Extended Coverage Tests")
class CuentaServiceExtendedTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    // ==================== crearCuenta Tests ====================
    
    @Test
    @DisplayName("Crear cuenta exitosamente")
    void debeCrearCuentaExitosamente() {
        // Arrange: Configurar el mock para que buscarPorId retorne Optional vacío (no existe)
        when(cuentaRepository.buscarPorId("C-001")).thenReturn(Optional.empty());

        // Act: Llamar al método crearCuenta con ID "C-001" y saldo inicial 5000
        cuentaService.crearCuenta("C-001", 5000);

        // Assert: Verificar que se llamó a buscarPorId para validar que no existe
        verify(cuentaRepository).buscarPorId("C-001");
        // Verificar que se llamó a guardar con cualquier objeto Cuenta
        verify(cuentaRepository).guardar(any(Cuenta.class));
    }

    @Test
    @DisplayName("No permitir crear cuenta duplicada")
    void noDebeCrearCuentaDuplicada() {
        // Arrange: Crear una cuenta existente y configurar el mock para que la retorne
        Cuenta cuentaExistente = new Cuenta("C-EXISTING", 1000);
        // Mock buscarPorId para retornar la cuenta existente (Optional con valor)
        when(cuentaRepository.buscarPorId("C-EXISTING")).thenReturn(Optional.of(cuentaExistente));

        // Act & Assert: Intentar crear cuenta con ID duplicado y capturar excepción
        // Se espera que lance IllegalArgumentException porque la cuenta ya existe
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.crearCuenta("C-EXISTING", 5000)
        );

        // Verificar que el mensaje de error es exactamente el esperado
        assertEquals("Ya existe una cuenta con el ID: C-EXISTING", exception.getMessage());
        // Verificar que guardar NO se fue llamado (never()) porque falló por duplicado
        verify(cuentaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear cuenta con saldo inicial cero")
    void debeCrearCuentaConSaldoCero() {
        // Arrange: Configurar mock para que no exista cuenta con ID "C-ZERO"
        when(cuentaRepository.buscarPorId("C-ZERO")).thenReturn(Optional.empty());

        // Act: Crear cuenta con saldo inicial cero (caso límite válido)
        cuentaService.crearCuenta("C-ZERO", 0);

        // Assert: Verificar que se guardó la cuenta a pesar de tener saldo cero
        verify(cuentaRepository).guardar(any(Cuenta.class));
    }

    // ==================== Depositar Tests ====================

    @Test
    @DisplayName("Depositar monto válido")
    void debeDepositarMontoValido() {
        // Arrange: Crear una cuenta con 1000 de saldo inicial
        Cuenta cuenta = new Cuenta("C-DEP-001", 1000);
        // Configurar mock para retornar la cuenta cuando se busque por ID
        when(cuentaRepository.buscarPorId("C-DEP-001")).thenReturn(Optional.of(cuenta));

        // Act: Depositar 500 a la cuenta (1000 + 500 = 1500)
        cuentaService.depositar("C-DEP-001", 500);

        // Assert: Verificar que el saldo es correcto después del depósito
        assertEquals(1500, cuenta.getSaldo());
        // Verificar que se guardó la cuenta en el repositorio
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("Depositar montos múltiples consecutivos")
    void debeDepositarMultiplesMontos() {
        // Arrange: Crear cuenta con 100 de saldo inicial
        Cuenta cuenta = new Cuenta("C-DEP-002", 100);
        // Configurar mock para retornar la misma cuenta en cada búsqueda
        when(cuentaRepository.buscarPorId("C-DEP-002")).thenReturn(Optional.of(cuenta));

        // Act: Realizar tres depósitos consecutivos
        // Depósito 1: 100 + 50 = 150
        cuentaService.depositar("C-DEP-002", 50);
        // Depósito 2: 150 + 75 = 225
        cuentaService.depositar("C-DEP-002", 75);
        // Depósito 3: 225 + 25 = 250
        cuentaService.depositar("C-DEP-002", 25);

        // Assert: Verificar que el saldo final es correcto
        assertEquals(250, cuenta.getSaldo());
        // Verificar que guardar se llamó exactamente 3 veces (una por cada depósito)
        verify(cuentaRepository, times(3)).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir deposito negativo")
    void noDebePermitirDepositoNegativo() {
        // Arrange: Crear cuenta con 1000 de saldo
        Cuenta cuenta = new Cuenta("C-DEP-NEG", 1000);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-DEP-NEG")).thenReturn(Optional.of(cuenta));

        // Act & Assert: Intentar depositar monto negativo (-100) y capturar excepción
        // Se espera IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-DEP-NEG", -100)
        );

        // Verificar que guardar NO se llamó porque el depósito fue rechazado
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir deposito cero")
    void noDebePermitirDepositoCero() {
        // Arrange: Crear cuenta con 1000 de saldo
        Cuenta cuenta = new Cuenta("C-DEP-ZERO", 1000);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-DEP-ZERO")).thenReturn(Optional.of(cuenta));

        // Act & Assert: Intentar depositar cero y capturar excepción
        // Se espera IllegalArgumentException porque el monto debe ser mayor que cero
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-DEP-ZERO", 0)
        );

        // Verificar que guardar NO se llamó porque el depósito fue rechazado
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir depositar en cuenta inexistente")
    void noDebeDepositarEnCuentaInexistente() {
        // Arrange: Configurar mock para retornar Optional vacío (no existe la cuenta)
        when(cuentaRepository.buscarPorId("C-NO-EXISTE")).thenReturn(Optional.empty());

        // Act & Assert: Intentar depositar en una cuenta inexistente y capturar excepción
        // Se espera IllegalArgumentException con mensaje específico
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-NO-EXISTE", 500)
        );

        // Verificar el mensaje de error exacto
        assertEquals("Cuenta no encontrada: C-NO-EXISTE", exception.getMessage());
        // Verificar que guardar NO se llamó porque la cuenta no existe
        verify(cuentaRepository, never()).guardar(any());
    }

    // ==================== Retirar Tests ====================

    @Test
    @DisplayName("Retirar monto válido")
    void debeRetirarMontoValido() {
        // Arrange: Crear cuenta con 2000 de saldo inicial
        Cuenta cuenta = new Cuenta("C-RET-001", 2000);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-RET-001")).thenReturn(Optional.of(cuenta));

        // Act: Retirar 750 (2000 - 750 = 1250)
        cuentaService.retirar("C-RET-001", 750);

        // Assert: Verificar que el saldo es correcto después del retiro
        assertEquals(1250, cuenta.getSaldo());
        // Verificar que se guardó la cuenta actualizada
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("Retirar todo el saldo")
    void debeRetirarTodoElSaldo() {
        // Arrange: Crear cuenta con 500 de saldo
        Cuenta cuenta = new Cuenta("C-RET-ALL", 500);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-RET-ALL")).thenReturn(Optional.of(cuenta));

        // Act: Retirar el saldo total (500 - 500 = 0)
        cuentaService.retirar("C-RET-ALL", 500);

        // Assert: Verificar que el saldo es cero después del retiro total
        assertEquals(0, cuenta.getSaldo());
        // Verificar que se guardó la cuenta con saldo cero
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retirar más saldo disponible")
    void noDebeRetirarMasDelSaldo() {
        // Arrange: Crear cuenta con 300 de saldo
        Cuenta cuenta = new Cuenta("C-RET-INSUF", 300);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-RET-INSUF")).thenReturn(Optional.of(cuenta));

        // Act & Assert: Intentar retirar 500 cuando hay solo 300 y capturar excepción
        // Se espera IllegalArgumentException por saldo insuficiente
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-INSUF", 500)
        );

        // Verificar el mensaje de error
        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        // Verificar que el saldo no cambió (se mantiene en 300)
        assertEquals(300, cuenta.getSaldo());
        // Verificar que guardar NO se llamó porque el retiro fue rechazado
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retiro negativo")
    void noDebePermitirRetiroNegativo() {
        // Arrange: Crear cuenta con 1000 de saldo
        Cuenta cuenta = new Cuenta("C-RET-NEG", 1000);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-RET-NEG")).thenReturn(Optional.of(cuenta));

        // Act & Assert: Intentar retirar monto negativo (-100) y capturar excepción
        // Se espera IllegalArgumentException
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-NEG", -100)
        );

        // Verificar que guardar NO se llamó porque el retiro fue rechazado
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retiro cero")
    void noDebePermitirRetiroCero() {
        // Arrange: Crear cuenta con 1000 de saldo
        Cuenta cuenta = new Cuenta("C-RET-ZERO", 1000);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-RET-ZERO")).thenReturn(Optional.of(cuenta));

        // Act & Assert: Intentar retirar cero y capturar excepción
        // Se espera IllegalArgumentException porque el monto debe ser mayor que cero
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-ZERO", 0)
        );

        // Verificar que guardar NO se llamó porque el retiro fue rechazado
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retirar de cuenta inexistente")
    void noDebeRetirarDeCuentaInexistente() {
        // Arrange: Configurar mock para retornar Optional vacío (no existe la cuenta)
        when(cuentaRepository.buscarPorId("C-NO-EXISTE-RET")).thenReturn(Optional.empty());

        // Act & Assert: Intentar retirar de cuenta inexistente y capturar excepción
        // Se espera IllegalArgumentException con mensaje específico
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-NO-EXISTE-RET", 100)
        );

        // Verificar el mensaje de error exacto
        assertEquals("Cuenta no encontrada: C-NO-EXISTE-RET", exception.getMessage());
        // Verificar que guardar NO se llamó porque la cuenta no existe
        verify(cuentaRepository, never()).guardar(any());
    }

    // ==================== Consultar Saldo Tests ====================

    @Test
    @DisplayName("Consultar saldo correctamente")
    void debeConsultarSaldoCorrectamente() {
        // Arrange: Crear cuenta con 2500 de saldo
        Cuenta cuenta = new Cuenta("C-SALDO", 2500);
        // Configurar mock para retornar la cuenta
        when(cuentaRepository.buscarPorId("C-SALDO")).thenReturn(Optional.of(cuenta));

        // Act: Consultar el saldo de la cuenta (operación de lectura sin cambios)
        double saldo = cuentaService.consultarSaldo("C-SALDO");

        // Assert: Verificar que retorna el saldo correcto
        assertEquals(2500, saldo);
        // Verificar que se llamó a buscarPorId para obtener la cuenta
        verify(cuentaRepository).buscarPorId("C-SALDO");
        // Verificar que guardar NO se llamó (consulta no modifica, solo lectura)
        verify(cuentaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("No permitir consultar saldo de cuenta inexistente")
    void noDebeConsultarSaldoDeCuentaInexistente() {
        // Arrange: Configurar mock para retornar Optional vacío (no existe la cuenta)
        when(cuentaRepository.buscarPorId("C-NO-EXISTE-SALDO")).thenReturn(Optional.empty());

        // Act & Assert: Intentar consultar saldo de cuenta inexistente y capturar excepción
        // Se espera IllegalArgumentException con mensaje específico
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.consultarSaldo("C-NO-EXISTE-SALDO")
        );

        // Verificar el mensaje de error exacto
        assertEquals("Cuenta no encontrada: C-NO-EXISTE-SALDO", exception.getMessage());
    }

    // ==================== Integration-like Tests ====================

    @Test
    @DisplayName("Flujo completo: crear, depositar, retirar, consultar")
    void debePermitirFlujoCompleto() {
        // Arrange: Crear instancia de cuenta con 1000 de saldo inicial
        Cuenta cuenta = new Cuenta("C-FLUJO", 1000);
        // Configurar el mock para retornar la cuenta en todas las búsquedas posteriores
        when(cuentaRepository.buscarPorId("C-FLUJO")).thenReturn(Optional.of(cuenta));

        // Act & Assert - Paso 1: Depositar 500
        // 1000 + 500 = 1500
        cuentaService.depositar("C-FLUJO", 500);
        // Verificar saldo después del depósito
        assertEquals(1500, cuenta.getSaldo());

        // Paso 2: Retirar 300
        // 1500 - 300 = 1200
        cuentaService.retirar("C-FLUJO", 300);
        // Verificar saldo después del retiro
        assertEquals(1200, cuenta.getSaldo());

        // Paso 3: Consultar saldo
        // Obtener el saldo (operación de lectura)
        double saldo = cuentaService.consultarSaldo("C-FLUJO");
        // Verificar que el saldo consultado es correcto después de todas las operaciones
        assertEquals(1200, saldo);
    }
}
