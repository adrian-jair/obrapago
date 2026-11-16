@echo off
title ObraPago - Iniciar Sistema Completo
color 0A
echo ========================================================
echo       INICIANDO SISTEMA INTEGRAL OBRAPAGO
echo ========================================================
echo.
echo [1/3] Verificando Docker Desktop...
docker info >nul 2>&1
if %errorlevel% neq 0 (
    echo Iniciando Docker Desktop en segundo plano...
    start "" "C:\Users\adrian\AppData\Local\Programs\DockerDesktop\Docker Desktop.exe"
    echo Esperando a que el motor de Docker responda...
    :wait_docker
    timeout /t 3 /nobreak >nul
    docker info >nul 2>&1
    if %errorlevel% neq 0 goto wait_docker
)
echo [OK] Motor de Docker activo y respondiendo.
echo.
echo [2/3] Levantando Base de Datos Oracle, Keycloak, Backend y Frontend...
cd /d "d:\lp2g2"
docker compose up -d
echo.
echo [3/3] Abriendo el sistema en Google Chrome...
start "" "http://localhost:4200"
echo.
echo ========================================================
echo   TODOS LOS SERVICIOS ESTAN EN EJECUCION:
echo   - Frontend:    http://localhost:4200
echo   - Backend:     http://localhost:8080
echo   - Swagger API: http://localhost:8080/swagger-ui.html
echo   - Keycloak:    http://localhost:8081
echo   - Oracle DB:   localhost:1521/FREEPDB1
echo ========================================================
pause

