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
        // Arrange
        when(cuentaRepository.buscarPorId("C-001")).thenReturn(Optional.empty());

        // Act
        cuentaService.crearCuenta("C-001", 5000);

        // Assert
        verify(cuentaRepository).buscarPorId("C-001");
        verify(cuentaRepository).guardar(any(Cuenta.class));
    }

    @Test
    @DisplayName("No permitir crear cuenta duplicada")
    void noDebeCrearCuentaDuplicada() {
        // Arrange
        Cuenta cuentaExistente = new Cuenta("C-EXISTING", 1000);
        when(cuentaRepository.buscarPorId("C-EXISTING")).thenReturn(Optional.of(cuentaExistente));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.crearCuenta("C-EXISTING", 5000)
        );

        assertEquals("Ya existe una cuenta con el ID: C-EXISTING", exception.getMessage());
        verify(cuentaRepository, never()).guardar(any());
    }

    @Test
    @DisplayName("Crear cuenta con saldo inicial cero")
    void debeCrearCuentaConSaldoCero() {
        // Arrange
        when(cuentaRepository.buscarPorId("C-ZERO")).thenReturn(Optional.empty());

        // Act
        cuentaService.crearCuenta("C-ZERO", 0);

        // Assert
        verify(cuentaRepository).guardar(any(Cuenta.class));
    }

    // ==================== Depositar Tests ====================

    @Test
    @DisplayName("Depositar monto válido")
    void debeDepositarMontoValido() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-001", 1000);
        when(cuentaRepository.buscarPorId("C-DEP-001")).thenReturn(Optional.of(cuenta));

        // Act
        cuentaService.depositar("C-DEP-001", 500);

        // Assert
        assertEquals(1500, cuenta.getSaldo());
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("Depositar montos múltiples consecutivos")
    void debeDepositarMultiplesMontos() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-002", 100);
        when(cuentaRepository.buscarPorId("C-DEP-002")).thenReturn(Optional.of(cuenta));

        // Act
        cuentaService.depositar("C-DEP-002", 50);
        cuentaService.depositar("C-DEP-002", 75);
        cuentaService.depositar("C-DEP-002", 25);

        // Assert
        assertEquals(250, cuenta.getSaldo());
        verify(cuentaRepository, times(3)).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir deposito negativo")
    void noDebePermitirDepositoNegativo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-NEG", 1000);
        when(cuentaRepository.buscarPorId("C-DEP-NEG")).thenReturn(Optional.of(cuenta));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-DEP-NEG", -100)
        );

        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir deposito cero")
    void noDebePermitirDepositoCero() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-DEP-ZERO", 1000);
        when(cuentaRepository.buscarPorId("C-DEP-ZERO")).thenReturn(Optional.of(cuenta));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-DEP-ZERO", 0)
        );

        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir depositar en cuenta inexistente")
    void noDebeDepositarEnCuentaInexistente() {
        // Arrange
        when(cuentaRepository.buscarPorId("C-NO-EXISTE")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.depositar("C-NO-EXISTE", 500)
        );

        assertEquals("Cuenta no encontrada: C-NO-EXISTE", exception.getMessage());
        verify(cuentaRepository, never()).guardar(any());
    }

    // ==================== Retirar Tests ====================

    @Test
    @DisplayName("Retirar monto válido")
    void debeRetirarMontoValido() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-001", 2000);
        when(cuentaRepository.buscarPorId("C-RET-001")).thenReturn(Optional.of(cuenta));

        // Act
        cuentaService.retirar("C-RET-001", 750);

        // Assert
        assertEquals(1250, cuenta.getSaldo());
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("Retirar todo el saldo")
    void debeRetirarTodoElSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-ALL", 500);
        when(cuentaRepository.buscarPorId("C-RET-ALL")).thenReturn(Optional.of(cuenta));

        // Act
        cuentaService.retirar("C-RET-ALL", 500);

        // Assert
        assertEquals(0, cuenta.getSaldo());
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retirar más saldo disponible")
    void noDebeRetirarMasDelSaldo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-INSUF", 300);
        when(cuentaRepository.buscarPorId("C-RET-INSUF")).thenReturn(Optional.of(cuenta));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-INSUF", 500)
        );

        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        assertEquals(300, cuenta.getSaldo()); // Saldo sin cambios
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retiro negativo")
    void noDebePermitirRetiroNegativo() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-NEG", 1000);
        when(cuentaRepository.buscarPorId("C-RET-NEG")).thenReturn(Optional.of(cuenta));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-NEG", -100)
        );

        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retiro cero")
    void noDebePermitirRetiroCero() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-RET-ZERO", 1000);
        when(cuentaRepository.buscarPorId("C-RET-ZERO")).thenReturn(Optional.of(cuenta));

        // Act & Assert
        assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-RET-ZERO", 0)
        );

        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("No permitir retirar de cuenta inexistente")
    void noDebeRetirarDeCuentaInexistente() {
        // Arrange
        when(cuentaRepository.buscarPorId("C-NO-EXISTE-RET")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-NO-EXISTE-RET", 100)
        );

        assertEquals("Cuenta no encontrada: C-NO-EXISTE-RET", exception.getMessage());
        verify(cuentaRepository, never()).guardar(any());
    }

    // ==================== Consultar Saldo Tests ====================

    @Test
    @DisplayName("Consultar saldo correctamente")
    void debeConsultarSaldoCorrectamente() {
        // Arrange
        Cuenta cuenta = new Cuenta("C-SALDO", 2500);
        when(cuentaRepository.buscarPorId("C-SALDO")).thenReturn(Optional.of(cuenta));

        // Act
        double saldo = cuentaService.consultarSaldo("C-SALDO");

        // Assert
        assertEquals(2500, saldo);
        verify(cuentaRepository).buscarPorId("C-SALDO");
        verify(cuentaRepository, never()).guardar(any()); // Consultar no debe guardar
    }

    @Test
    @DisplayName("No permitir consultar saldo de cuenta inexistente")
    void noDebeConsultarSaldoDeCuentaInexistente() {
        // Arrange
        when(cuentaRepository.buscarPorId("C-NO-EXISTE-SALDO")).thenReturn(Optional.empty());

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.consultarSaldo("C-NO-EXISTE-SALDO")
        );

        assertEquals("Cuenta no encontrada: C-NO-EXISTE-SALDO", exception.getMessage());
    }

    // ==================== Integration-like Tests ====================

    @Test
    @DisplayName("Flujo completo: crear, depositar, retirar, consultar")
    void debePermitirFlujoCompleto() {
        // Arrange
        when(cuentaRepository.buscarPorId("C-FLUJO")).thenReturn(Optional.empty());
        when(cuentaRepository.buscarPorId("C-FLUJO")).thenReturn(
                Optional.empty(),
                Optional.of(new Cuenta("C-FLUJO", 1000)),
                Optional.of(new Cuenta("C-FLUJO", 1000)),
                Optional.of(new Cuenta("C-FLUJO", 1500))
        );

        // Act & Assert - Create
        cuentaService.crearCuenta("C-FLUJO", 1000);

        // Reimport para new instance
        Cuenta cuenta = new Cuenta("C-FLUJO", 1000);
        when(cuentaRepository.buscarPorId("C-FLUJO")).thenReturn(Optional.of(cuenta));

        // Deposit
        cuentaService.depositar("C-FLUJO", 500);
        assertEquals(1500, cuenta.getSaldo());

        // Withdraw
        cuentaService.retirar("C-FLUJO", 300);
        assertEquals(1200, cuenta.getSaldo());

        // Consult
        double saldo = cuentaService.consultarSaldo("C-FLUJO");
        assertEquals(1200, saldo);
    }
}
