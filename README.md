# BancoApp - Aplicación Bancaria Simple

![Java](https://img.shields.io/badge/Java-21-orange)
![Maven](https://img.shields.io/badge/Maven-3.9.13-blue)
![Tests](https://img.shields.io/badge/Tests-7%2F7%20passing-brightgreen)

Aplicación bancaria simple de consola con Java 21 y tests con Mockito.

## 📋 Características

- ✅ Crear cuentas bancarias
- ✅ Depositar dinero
- ✅ Retirar dinero
- ✅ Consultar saldo
- ✅ Validaciones de negocio
- ✅ Tests unitarios con Mockito (7/7 passing)
- ✅ Repositorio en memoria (sin base de datos)

## 🚀 Cómo ejecutar la aplicación

### Opción 1: Con Maven (recomendado)

```bash
mvn clean compile
mvn exec:java -Dexec.mainClass="com.bancoapp.BancoAppMain"
```

### Opción 2: Con el JAR compilado

```bash
# Compilar
mvn clean package

# Ejecutar
java -jar target/bancoapp-1.0-SNAPSHOT.jar
```

### Opción 3: Directamente con Java (desde la carpeta del proyecto)

```bash
# Compilar
mvn clean compile

# Ejecutar
java -cp target/classes com.bancoapp.BancoAppMain
```

## 🧪 Ejecutar los tests

```bash
# Todos los tests
mvn test

# Tests con reporte detallado
mvn clean test

# Verificar compilación y tests
mvn clean verify
```

## 📖 Uso de la aplicación

Al ejecutar la aplicación, verás un menú interactivo:

```
╔════════════════════════════════════════╗
║   BIENVENIDO A BANCOAPP - JAVA 21      ║
╚════════════════════════════════════════╝

┌────────────────────────────────────────┐
│           MENÚ PRINCIPAL               │
├────────────────────────────────────────┤
│  1. Crear cuenta                       │
│  2. Depositar dinero                   │
│  3. Retirar dinero                     │
│  4. Consultar saldo                    │
│  5. Salir                              │
└────────────────────────────────────────┘
```

### Ejemplo de uso:

1. **Crear una cuenta:**
   - Opción 1
   - ID: `CTA001`
   - Saldo inicial: `1000`

2. **Depositar:**
   - Opción 2
   - ID: `CTA001`
   - Monto: `500`

3. **Retirar:**
   - Opción 3
   - ID: `CTA001`
   - Monto: `200`

4. **Consultar saldo:**
   - Opción 4
   - ID: `CTA001`

## 📦 Estructura del proyecto

```
BancoApp/
├── src/
│   ├── main/java/com/bancoapp/
│   │   ├── BancoAppMain.java          # Punto de entrada con menú de consola
│   │   ├── model/
│   │   │   └── Cuenta.java            # Modelo de dominio
│   │   ├── repository/
│   │   │   ├── CuentaRepository.java      # Interfaz
│   │   │   └── CuentaRepositoryImpl.java  # Implementación en memoria
│   │   └── service/
│   │       └── CuentaService.java     # Lógica de negocio
│   └── test/java/com/bancoapp/
│       └── service/
│           ├── CuentaServiceTest.java # Tests con Mockito
│           └── CuentaTest.java
└── pom.xml
```

## 🧪 Tests con Mockito

El proyecto incluye tests unitarios usando Mockito para simular el repositorio:

```java
@Test
void debeDepositarCorrectamente() {
    // Given
    Cuenta cuenta = new Cuenta("123", 100);
    when(cuentaRepository.buscarPorId("123")).thenReturn(Optional.of(cuenta));
    
    // When
    cuentaService.depositar("123", 50);
    
    // Then
    verify(cuentaRepository).guardar(cuenta);
    assertEquals(150, cuenta.getSaldo());
}
```

**Cobertura actual: 7/7 tests passing (100%)**

## 🛠️ Tecnologías

- **Java 21 LTS** - Lenguaje de programación
- **Maven 3.9.13** - Gestión de dependencias y build
- **JUnit 5.12.1** - Framework de testing
- **Mockito 5.12.0** - Mocking para tests unitarios

## ✅ Validaciones implementadas

- ✅ ID de cuenta no puede ser vacío
- ✅ Saldo inicial no puede ser negativo
- ✅ Monto de depósito debe ser positivo
- ✅ Monto de retiro debe ser positivo
- ✅ Saldo insuficiente para retiro
- ✅ Cuenta no encontrada

## 🔄 Upgrade a Java 21

Este proyecto fue actualizado exitosamente de Java 17 a Java 21 LTS:
- **Fecha:** Marzo 6, 2026
- **Tests:** 7/7 passing (100%)
- **CVEs:** 0 vulnerabilidades detectadas
- **Documentación:** `.github/java-upgrade/20260306231538/`

## 📝 Notas

- El repositorio es **en memoria**, los datos se pierden al cerrar la aplicación
- Para persistencia permanente, considera agregar Spring Data JPA + H2/PostgreSQL
- Los tests usan Mockito para simular el repositorio sin necesidad de base de datos

## 🤝 Contribuciones

Este es un proyecto educativo simple. Para mejoras futuras considera:
- [ ] Agregar Spring Boot
- [ ] Persistencia con base de datos
- [ ] API REST
- [ ] Configurar JaCoCo para cobertura de código
- [ ] Agregar más validaciones de negocio

---

**⚡ Compilado con Java 21 LTS**
