package com.bancoapp;

import com.bancoapp.model.Cuenta;
import com.bancoapp.repository.CuentaRepositoryImpl;
import com.bancoapp.service.CuentaService;

import java.util.Scanner;

public class BancoAppMain {

    private static final CuentaService cuentaService = 
            new CuentaService(new CuentaRepositoryImpl());
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════╗");
        System.out.println("║   BIENVENIDO A BANCOAPP - JAVA 21      ║");
        System.out.println("╚════════════════════════════════════════╝");
        
        boolean continuar = true;
        
        while (continuar) {
            mostrarMenu();
            int opcion = leerOpcion();
            
            try {
                switch (opcion) {
                    case 1 -> crearCuenta();
                    case 2 -> depositar();
                    case 3 -> retirar();
                    case 4 -> consultarSaldo();
                    case 5 -> {
                        System.out.println("\n¡Gracias por usar BancoApp! ");
                        continuar = false;
                    }
                    default -> System.out.println("Opción inválida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Error: " + e.getMessage());
            } catch (Exception e) {
                System.out.println("Error inesperado: " + e.getMessage());
            }
            
            if (continuar) {
                System.out.println("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }

    private static void mostrarMenu() {
        System.out.println("\n┌────────────────────────────────────────┐");
        System.out.println("│           MENÚ PRINCIPAL               │");
        System.out.println("├────────────────────────────────────────┤");
        System.out.println("│  1. Crear cuenta                       │");
        System.out.println("│  2. Depositar dinero                   │");
        System.out.println("│  3. Retirar dinero                     │");
        System.out.println("│  4. Consultar saldo                    │");
        System.out.println("│  5. Salir                              │");
        System.out.println("└────────────────────────────────────────┘");
        System.out.print("Seleccione una opción: ");
    }

    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private static void crearCuenta() {
        System.out.println("\n═══════ CREAR NUEVA CUENTA ═══════");
        System.out.print("Ingrese el ID de la cuenta: ");
        String idCuenta = scanner.nextLine().trim();
        
        System.out.print("Ingrese el saldo inicial: $");
        double saldoInicial = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.crearCuenta(idCuenta, saldoInicial);
        
        System.out.println("Cuenta creada exitosamente!");
        System.out.println("   ID: " + idCuenta);
        System.out.println("   Saldo inicial: $" + String.format("%.2f", saldoInicial));
    }

    private static void depositar() {
        System.out.println("\n═══════ DEPOSITAR DINERO ═══════");
        System.out.print("Ingrese el ID de la cuenta: ");
        String idCuenta = scanner.nextLine().trim();
        
        System.out.print("Ingrese el monto a depositar: $");
        double monto = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.depositar(idCuenta, monto);
        double nuevoSaldo = cuentaService.consultarSaldo(idCuenta);
        
        System.out.println("Depósito realizado exitosamente!");
        System.out.println("   Monto depositado: $" + String.format("%.2f", monto));
        System.out.println("   Nuevo saldo: $" + String.format("%.2f", nuevoSaldo));
    }

    private static void retirar() {
        System.out.println("\n═══════ RETIRAR DINERO ═══════");
        System.out.print("Ingrese el ID de la cuenta: ");
        String idCuenta = scanner.nextLine().trim();
        
        double saldoActual = cuentaService.consultarSaldo(idCuenta);
        System.out.println("   Saldo disponible: $" + String.format("%.2f", saldoActual));
        
        System.out.print("Ingrese el monto a retirar: $");
        double monto = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.retirar(idCuenta, monto);
        double nuevoSaldo = cuentaService.consultarSaldo(idCuenta);
        
        System.out.println("Retiro realizado exitosamente!");
        System.out.println("   Monto retirado: $" + String.format("%.2f", monto));
        System.out.println("   Nuevo saldo: $" + String.format("%.2f", nuevoSaldo));
    }

    private static void consultarSaldo() {
        System.out.println("\n═══════ CONSULTAR SALDO ═══════");
        System.out.print("Ingrese el ID de la cuenta: ");
        String idCuenta = scanner.nextLine().trim();
        
        double saldo = cuentaService.consultarSaldo(idCuenta);
        
        System.out.println("Consulta realizada exitosamente!");
        System.out.println("   ID: " + idCuenta);
        System.out.println("   Saldo actual: $" + String.format("%.2f", saldo));
    }
}

//# Para ejecutar tests
//$env:JAVA_HOME = "C:\Users\fredi\.jdk\jdk-21.0.8" ; C:\Users\fredi\.maven\maven-3.9.13\bin\mvn.cmd test

//# Para limpiar + tests
//$env:JAVA_HOME = "C:\Users\fredi\.jdk\jdk-21.0.8" ; C:\Users\fredi\.maven\maven-3.9.13\bin\mvn.cmd clean test

//# Para compilar
//$env:JAVA_HOME = "C:\Users\fredi\.jdk\jdk-21.0.8" ; C:\Users\fredi\.maven\maven-3.9.13\bin\mvn.cmd compile

//# Para empaquetar
//$env:JAVA_HOME = "C:\Users\fredi\.jdk\jdk-21.0.8" ; C:\Users\fredi\.maven\maven-3.9.13\bin\mvn.cmd package