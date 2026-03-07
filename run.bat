@echo off
echo ╔════════════════════════════════════════╗
echo ║   BancoApp - Iniciando aplicación...  ║
echo ╚════════════════════════════════════════╝
echo.

REM Configurar Java 21
set JAVA_HOME=C:\Users\fredi\.jdk\jdk-21.0.8

REM Verificar si existe el JAR
if exist "target\bancoapp-1.0-SNAPSHOT.jar" (
    echo [INFO] Ejecutando desde JAR compilado...
    "%JAVA_HOME%\bin\java.exe" -jar target\bancoapp-1.0-SNAPSHOT.jar
) else (
    echo [INFO] Compilando aplicación...
    C:\Users\fredi\.maven\maven-3.9.13\bin\mvn.cmd clean compile
    if %errorlevel% equ 0 (
        echo [INFO] Compilación exitosa. Ejecutando aplicación...
        "%JAVA_HOME%\bin\java.exe" -cp target\classes com.bancoapp.BancoAppMain
    ) else (
        echo [ERROR] Error en la compilación
        pause
        exit /b 1
    )
)

pause
