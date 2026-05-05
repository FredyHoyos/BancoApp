package com.bancoapp;

import com.bancoapp.repository.CuentaRepositoryImpl;
import com.bancoapp.service.CuentaService;

import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BancoAppMain {

    private static final CuentaService cuentaService = 
            new CuentaService(new CuentaRepositoryImpl());
    private static final Scanner scanner = new Scanner(System.in);

    private static Logger logger = Logger.getLogger(BancoAppMain.class.getName());

    @SuppressWarnings("java:S7467")
    public static void main(String[] args) {
        int argsCount = args == null ? 0 : args.length;
        logger.log(Level.INFO, "Iniciando BancoApp - Java 25 (args={0})", argsCount);

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
                        logger.info("Saliendo de BancoApp");
                        continuar = false;
                    }
                    default -> logger.warning("Opción inválida. Intente nuevamente.");
                }
            } catch (IllegalArgumentException e) {
                logger.severe("Error: " + e.getMessage());
            } catch (Exception e) {
                logger.log(Level.SEVERE, "Error inesperado", e);
            }
            
            if (continuar) {
                logger.info("\nPresione Enter para continuar...");
                scanner.nextLine();
            }
        }
        
        scanner.close();
    }

    private static void mostrarMenu() {
        logger.info("\n┌────────────────────────────────────────┐");
        logger.info("│           BIENVENIDO A BANCOAPP        │");  
        logger.info("├────────────────────────────────────────┤");
        logger.info("│  1. Crear cuenta                       │");
        logger.info("│  2. Depositar dinero                   │");
        logger.info("│  3. Retirar dinero                     │");
        logger.info("│  4. Consultar saldo                    │");
        logger.info("│  5. Salir                              │");
        logger.info("└────────────────────────────────────────┘");
        logger.info("Seleccione una opción:");
    }

    @SuppressWarnings("java:S7467")
    private static int leerOpcion() {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException ignored) {
            return -1;
        }
    }

    private static String mensaje = "Ingrese el ID de la cuenta: ";
    private static void crearCuenta() {
        logger.info("\n═══════ CREAR NUEVA CUENTA ═══════");
        logger.info(mensaje);
        String idCuenta = scanner.nextLine().trim();
        
        logger.info("Ingrese el saldo inicial: $");
        double saldoInicial = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.crearCuenta(idCuenta, saldoInicial);
        
        logger.info("Cuenta creada exitosamente!");
        logger.log(Level.INFO, () -> String.format("   ID: %s", idCuenta));
        logger.log(Level.INFO, "   Saldo inicial: ${0,number,0.00}", saldoInicial);
    }

    private static void depositar() {
        logger.info("\n═══════ DEPOSITAR DINERO ═══════");
        logger.info(mensaje);
        String idCuenta = scanner.nextLine().trim();
        
        logger.info("Ingrese el monto a depositar: $");
        double monto = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.depositar(idCuenta, monto);
        double nuevoSaldo = cuentaService.consultarSaldo(idCuenta);
        
        logger.info("Depósito realizado exitosamente!");
        logger.log(Level.INFO, "   Monto depositado: ${0,number,0.00}", monto);
        logger.log(Level.INFO, "   Nuevo saldo: ${0,number,0.00}", nuevoSaldo);
    }

    private static void retirar() {
        logger.info("\n═══════ RETIRAR DINERO ═══════");
        logger.info(mensaje);
        String idCuenta = scanner.nextLine().trim();
        
        double saldoActual = cuentaService.consultarSaldo(idCuenta);
        logger.log(Level.INFO, "   Saldo disponible: ${0,number,0.00}", saldoActual);
        
        logger.info("Ingrese el monto a retirar: $");
        double monto = Double.parseDouble(scanner.nextLine().trim());
        
        cuentaService.retirar(idCuenta, monto);
        double nuevoSaldo = cuentaService.consultarSaldo(idCuenta);
        
        logger.info("Retiro realizado exitosamente!");
        logger.log(Level.INFO, "   Monto retirado: ${0,number,0.00}", monto);
        logger.log(Level.INFO, "   Nuevo saldo: ${0,number,0.00}", nuevoSaldo);
    }

    private static void consultarSaldo() {
        logger.info("\n═══════ CONSULTAR SALDO ═══════");
        logger.info(mensaje);
        String idCuenta = scanner.nextLine().trim();
        
        double saldo = cuentaService.consultarSaldo(idCuenta);
        
        logger.info("Consulta realizada exitosamente!");
        logger.log(Level.INFO, "   ID: {0}", idCuenta);
        logger.log(Level.INFO, "   Saldo actual: ${0,number,0.00}", saldo);
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