package com.bancoapp.service;

import com.bancoapp.model.Cuenta;
import com.bancoapp.repository.CuentaRepository;
import org.junit.jupiter.api.DisplayName; // Para dar nombres descriptivos a los tests
import org.junit.jupiter.api.Test; // Anotación para marcar métodos de test
import org.junit.jupiter.api.extension.ExtendWith; // Para extender JUnit con Mockito (habilitar mocks en los tests)
import org.mockito.InjectMocks; // Para inyectar los mocks en el servicio
import org.mockito.Mock; // Para crear objetos simulados (mocks) del repositorio
import org.mockito.junit.jupiter.MockitoExtension; // Extensión de Mockito para JUnit 5

import java.util.Optional; // Para manejar resultados que pueden ser "vacíos" (como buscar una cuenta que no existe)

import static org.junit.jupiter.api.Assertions.assertEquals; // Para verificar que los resultados son los esperados
import static org.junit.jupiter.api.Assertions.assertThrows; // Para verificar que se lanzan excepciones cuando se esperan errores
import static org.mockito.Mockito.never; // Para verificar que un método NO fue llamado
import static org.mockito.Mockito.times; // Para verificar cuántas veces fue llamado un método (por ejemplo, guardar la cuenta)
import static org.mockito.Mockito.verify; // Para verificar que el servicio interactúa correctamente con el repositorio (por ejemplo, que llamó a guardar o buscar)
import static org.mockito.Mockito.when; // Para configurar el comportamiento del mock (por ejemplo, qué devuelve cuando se busca una cuenta por ID)

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaRepository cuentaRepository;

    @InjectMocks
    private CuentaService cuentaService;

    @Test
    @DisplayName("Depositar dinero correctamente (Unit Test simple)")
    void debeDepositarDineroCorrectamente() {
        // AAA - Arrange
        Cuenta cuenta = new Cuenta("C-100", 1000);
        when(cuentaRepository.buscarPorId("C-100")).thenReturn(Optional.of(cuenta));

        // AAA - Act
        cuentaService.depositar("C-100", 500);

        // AAA - Assert
        assertEquals(1500, cuenta.getSaldo());
        // Mockito: verificamos colaboración con el repositorio simulado
        verify(cuentaRepository, times(1)).guardar(cuenta);
    }

    @Test
    @DisplayName("Retirar dinero correctamente (Unit Test simple)")
    void debeRetirarDineroCorrectamente() {
        // AAA - Arrange
        Cuenta cuenta = new Cuenta("C-200", 1200);
        when(cuentaRepository.buscarPorId("C-200")).thenReturn(Optional.of(cuenta));

        // AAA - Act
        cuentaService.retirar("C-200", 200);

        // AAA - Assert
        assertEquals(1000, cuenta.getSaldo());
        verify(cuentaRepository).guardar(cuenta);
    }

    @Test
    @DisplayName("Evitar retiro con saldo insuficiente")
    void noDebePermitirRetiroConSaldoInsuficiente() {
        // TDD (paso 1): este test se escribe antes de implementar la validación en Cuenta.retirar

        // BDD style comment:
        // Given una cuenta con saldo 300
        Cuenta cuenta = new Cuenta("C-300", 300);
        when(cuentaRepository.buscarPorId("C-300")).thenReturn(Optional.of(cuenta));

        // When se intenta retirar 500
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.retirar("C-300", 500)
        );

        // Then se lanza error y no se guarda la cuenta
        assertEquals("Saldo insuficiente para retirar", exception.getMessage());
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("Evitar montos negativos o cero")
    void noDebePermitirMontosNegativosOCero() {
        // AAA - Arrange
        Cuenta cuenta = new Cuenta("C-400", 800);
        when(cuentaRepository.buscarPorId("C-400")).thenReturn(Optional.of(cuenta));

        // AAA - Act + Assert
        assertThrows(IllegalArgumentException.class, () -> cuentaService.depositar("C-400", -10));
        assertThrows(IllegalArgumentException.class, () -> cuentaService.retirar("C-400", 0));

        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("Consultar saldo (Unit Test complejo con Mockito)")
    void debeConsultarSaldo() {
        // Este test es más "complejo" que uno básico porque valida:
        // 1) retorno correcto, 2) interacción exacta con dependencia, 3) ausencia de guardado.

        // AAA - Arrange
        Cuenta cuenta = new Cuenta("C-500", 2450.75);
        when(cuentaRepository.buscarPorId("C-500")).thenReturn(Optional.of(cuenta));

        // AAA - Act
        double saldo = cuentaService.consultarSaldo("C-500");

        // AAA - Assert
        assertEquals(2450.75, saldo);
        verify(cuentaRepository, times(1)).buscarPorId("C-500");
        verify(cuentaRepository, never()).guardar(cuenta);
    }

    @Test
    @DisplayName("Crear cuenta nueva correctamente")
    void debeCrearCuentaNueva() {
        // AAA - Arrange
        when(cuentaRepository.buscarPorId("C-600")).thenReturn(Optional.empty());

        // AAA - Act
        cuentaService.crearCuenta("C-600", 1000);

        // AAA - Assert
        verify(cuentaRepository, times(1)).buscarPorId("C-600");
        // Verificar que guardó la nueva cuenta
        verify(cuentaRepository, times(1)).guardar(org.mockito.ArgumentMatchers.any(Cuenta.class));
    }

    @Test
    @DisplayName("No permitir crear cuenta duplicada")
    void noDebePermitirCrearCuentaDuplicada() {
        // AAA - Arrange
        Cuenta cuentaExistente = new Cuenta("C-700", 500);
        when(cuentaRepository.buscarPorId("C-700")).thenReturn(Optional.of(cuentaExistente));

        // AAA - Act + Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> cuentaService.crearCuenta("C-700", 1000)
        );

        // Verificar el mensaje de error
        assertEquals("Ya existe una cuenta con el ID: C-700", exception.getMessage());
        // Verificar que NO se guardó ninguna cuenta nueva
        verify(cuentaRepository, never()).guardar(org.mockito.ArgumentMatchers.any(Cuenta.class));
    }
}
